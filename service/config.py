import sys
import os

class ConfigService:
    def __init__(self):
        if sys.platform == 'linux':
            self.__config_dir = os.environ['HOME'] + '/.config/webshark'
        
    def create_config_dir(self):
        os.makedirs(self.__config_dir, exist_ok=True)
        os.makedirs(self.__config_dir + '/cached_certs', exist_ok=True)

    def get_config_dir(self):
        return self.__config_dir