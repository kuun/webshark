from dependency_injector import containers, providers

from service.ca.ca_service import CAService


class Container(containers.DeclarativeContainer):
    ca_service = providers.Singleton(CAService)