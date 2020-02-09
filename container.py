from dependency_injector import containers, providers

from model.history_model import HistoryModel
from service.ca.ca_service import CAService
from service.config import ConfigService
from service.proxy.proxy_service import ProxyService


class Container(containers.DeclarativeContainer):
    config_service = providers.Singleton(ConfigService)
    ca_service = providers.Singleton(CAService, config_service)
    history_model = providers.Singleton(HistoryModel)
    proxy_service = providers.Singleton(ProxyService, ca_service, history_model)

