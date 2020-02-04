import datetime
import logging
import ssl
import traceback

from cryptography import x509
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives.serialization import load_pem_private_key
from cryptography.x509 import NameOID

log = logging.getLogger(__name__)


class CAService:
    def __init__(self, config_service):
        self.config_dir = config_service.get_config_dir()
        self.CA_KEY_FILE = self.config_dir + '/ca-key.pem'
        self.CA_CERT_FILE = self.config_dir + '/ca-cert.pem'
        self.CA_KEY_PASSWORD = b'webshark'
        self.ca_key, self.ca_cert = self.__load_ca()
        self.ca_issuer = x509.Name([
            x509.NameAttribute(NameOID.COUNTRY_NAME, 'US'),
            x509.NameAttribute(NameOID.STATE_OR_PROVINCE_NAME, 'California'),
            x509.NameAttribute(NameOID.LOCALITY_NAME, 'San Francisco'),
            x509.NameAttribute(NameOID.ORGANIZATION_NAME, 'Webshark'),
            x509.NameAttribute(NameOID.ORGANIZATIONAL_UNIT_NAME, 'Webshark'),
            x509.NameAttribute(NameOID.COMMON_NAME, 'Webshark Test Root CA'),
        ])
        if self.ca_key is None or self.ca_cert is None:
            self.ca_key, self.ca_cert = self.generate_ca(key_size=4096)
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
        context.load_cert_chain(self.build_cert_filename(hostname), self.CA_KEY_FILE, self.CA_KEY_PASSWORD)
        self.ssl_contexts[hostname] = context
        return context

    def generate_ca(self, key_size=2048,
                    cert_expire=3650):
        key = rsa.generate_private_key(
            public_exponent=65537,
            key_size=key_size,
            backend=default_backend()
        )
        subject = issuer = self.ca_issuer
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
            x509.SubjectKeyIdentifier.from_public_key(key.public_key()), critical=False
        ).add_extension(
            x509.AuthorityKeyIdentifier.from_issuer_public_key(key.public_key()), critical=False
        ).add_extension(
            x509.BasicConstraints(True, None), critical=True,
        ).sign(key, hashes.SHA256(), default_backend())

        # write key and certificate to file
        with open(self.CA_KEY_FILE, 'wb') as f:
            f.write(key.private_bytes(
                encoding=serialization.Encoding.PEM,
                format=serialization.PrivateFormat.TraditionalOpenSSL,
                encryption_algorithm=serialization.BestAvailableEncryption(self.CA_KEY_PASSWORD),
            ))
        with open(self.CA_CERT_FILE, 'wb') as f:
            f.write(cert.public_bytes(serialization.Encoding.PEM))
        log.warning('new ca generated.')
        return key, cert

    def create_certificate(self, hostname, cert_expire=3650):
        subject = x509.Name([
            self.ca_issuer.get_attributes_for_oid(NameOID.COUNTRY_NAME)[0],
            self.ca_issuer.get_attributes_for_oid(NameOID.STATE_OR_PROVINCE_NAME)[0],
            self.ca_issuer.get_attributes_for_oid(NameOID.LOCALITY_NAME)[0],
            self.ca_issuer.get_attributes_for_oid(NameOID.ORGANIZATION_NAME)[0],
            self.ca_issuer.get_attributes_for_oid(NameOID.ORGANIZATIONAL_UNIT_NAME)[0],
            x509.NameAttribute(NameOID.COMMON_NAME, hostname),
        ])
        now = datetime.datetime.utcnow()
        cert = x509.CertificateBuilder().subject_name(
            subject
        ).issuer_name(
            self.ca_issuer
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
        log.info('succeed to generate certificate for host: %s', hostname)

    def build_cert_filename(self, hostname):
        return '{}/cached_certs/{}-cert.pem'.format(self.config_dir, hostname)

    def __load_ca(self):
        try:
            with open(self.CA_KEY_FILE, 'rb') as file:
                pem_data = file.read()
            ca_key = load_pem_private_key(pem_data, self.CA_KEY_PASSWORD, default_backend())
            with open(self.CA_CERT_FILE, 'rb') as file:
                pem_data = file.read()
            ca_cert = x509.load_pem_x509_certificate(pem_data, default_backend())
            return ca_key, ca_cert
        except Exception:
            print('can not load ca key and cert, error:', traceback.format_exc())
            return None, None
