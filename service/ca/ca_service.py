import datetime
import logging
import ssl
import traceback

from cryptography import x509
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization, hashes
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives.serialization import load_pem_private_key
from cryptography.x509 import NameOID

CA_KEY_FILE = 'config/ca-key.pem'
CA_CERT_FILE = 'config/ca-cert.pem'
CA_KEY_PASSWORD = b'webshark'

log = logging.getLogger(__name__)

def load_ca():
    try:
        with open(CA_KEY_FILE, 'rb') as file:
            pem_data = file.read()
        ca_key = load_pem_private_key(pem_data, CA_KEY_PASSWORD, default_backend())
        with open(CA_CERT_FILE, 'rb') as file:
            pem_data = file.read()
        ca_cert = x509.load_pem_x509_certificate(pem_data, default_backend())
        return ca_key, ca_cert
    except Exception as e:
        print('can not load ca key and cert, error:', traceback.format_exc())
        return None, None


def generate_ca(key_size=2048,
                cert_expire=3650,
                country_name='US',
                state_name='California',
                city_name='San Francisco',
                organization_name='Example Company',
                common_name='example.com'):
    key = rsa.generate_private_key(
        public_exponent=65537,
        key_size=key_size,
        backend=default_backend()
    )
    subject = issuer = x509.Name([
        x509.NameAttribute(NameOID.COUNTRY_NAME, country_name),
        x509.NameAttribute(NameOID.STATE_OR_PROVINCE_NAME, state_name),
        x509.NameAttribute(NameOID.LOCALITY_NAME, city_name),
        x509.NameAttribute(NameOID.ORGANIZATION_NAME, organization_name),
        x509.NameAttribute(NameOID.COMMON_NAME, common_name),
    ])
    now = datetime.datetime.utcnow()
    cert = x509.CertificateBuilder().subject_name(
        subject
    ).issuer_name(
        issuer
    ).public_key(
        key.public_key()
    ).serial_number(
        x509.random_serial_number()
    ).not_valid_before(
        now
    ).not_valid_after(
        now + datetime.timedelta(days=cert_expire)
    ).add_extension(
        x509.SubjectAlternativeName([x509.DNSName('localhost')]),
        critical=False,
    ).sign(key, hashes.SHA256(), default_backend())

    # write key and certificate to file
    with open(CA_KEY_FILE, 'wb') as f:
        f.write(key.private_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PrivateFormat.TraditionalOpenSSL,
            encryption_algorithm=serialization.BestAvailableEncryption(CA_KEY_PASSWORD),
        ))
    with open(CA_CERT_FILE, 'wb') as f:
        f.write(cert.public_bytes(serialization.Encoding.PEM))
    return key, cert



class CAService:
    def __init__(self):
        self.ca_key, self.ca_cert = load_ca()
        if self.ca_key is None or self.ca_cert is None:
            self.ca_key, self.ca_cert = generate_ca()
        self.ssl_contexts = {}

    def get_ca_cert(self):
        return self.ca_cert

    def get_ca_key(self) -> x509.Certificate:
        return self.ca_key

    def get_ssl_context(self, hostname) -> ssl.SSLContext:
        context = self.ssl_contexts.get(hostname)
        if context is not None:
            return context

        self.create_certificate(hostname)
        context = ssl.create_default_context(purpose=ssl.Purpose.CLIENT_AUTH)
        context.load_cert_chain(self.build_cert_filename(hostname), CA_KEY_FILE, CA_KEY_PASSWORD)
        self.ssl_contexts[hostname] = context
        return context

    def create_certificate(self, hostname, cert_expire=3650, country_name='US',
                           state_name='California',
                           city_name='San Francisco',
                           organization_name='Example Company',
                           ca_common_name='example.com'):
        issuer = x509.Name([
            x509.NameAttribute(NameOID.COUNTRY_NAME, country_name),
            x509.NameAttribute(NameOID.STATE_OR_PROVINCE_NAME, state_name),
            x509.NameAttribute(NameOID.LOCALITY_NAME, city_name),
            x509.NameAttribute(NameOID.ORGANIZATION_NAME, organization_name),
            x509.NameAttribute(NameOID.COMMON_NAME, ca_common_name),
        ])
        subject = x509.Name([
            x509.NameAttribute(NameOID.COUNTRY_NAME, country_name),
            x509.NameAttribute(NameOID.STATE_OR_PROVINCE_NAME, state_name),
            x509.NameAttribute(NameOID.LOCALITY_NAME, city_name),
            x509.NameAttribute(NameOID.ORGANIZATION_NAME, organization_name),
            x509.NameAttribute(NameOID.COMMON_NAME, hostname),
        ])
        now = datetime.datetime.utcnow()
        cert = x509.CertificateBuilder().subject_name(
            subject
        ).issuer_name(
            issuer
        ).public_key(
            self.ca_key.public_key()
        ).serial_number(
            x509.random_serial_number()
        ).not_valid_before(
            now
        ).not_valid_after(
            now + datetime.timedelta(days=cert_expire)
        ).add_extension(
            x509.SubjectAlternativeName([x509.DNSName(hostname)]),
            critical=False,
        ).sign(self.ca_key, hashes.SHA256(), default_backend())
        with open(self.build_cert_filename(hostname), 'wb') as f:
            f.write(cert.public_bytes(serialization.Encoding.PEM))

    @staticmethod
    def build_cert_filename(hostname):
        return 'config/cached_certs/' + hostname + '-cert.pem'



