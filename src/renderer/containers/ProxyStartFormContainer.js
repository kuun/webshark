import {connect} from 'react-redux';
import {withRouter} from 'react-router';
import {startProxy} from "../actions";
import ProxyStartForm from "../components/ProxyStartForm";

const mapStateToProps = state => {
  return {
    addr: state.proxyServer.addr,
    port: state.proxyServer.port
  }
};

const mapDispatchToProps = dispatch => {
  return {
    onStart: (addr: string, port: number) => {
      dispatch(startProxy(addr, port));
    }
  }
};

const container = connect(
  mapStateToProps,
  mapDispatchToProps
)(ProxyStartForm);

export default withRouter(container);
