package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import dao.HibernateDAOCliente;
import dto.ClienteDTO;
import entities.Cliente;

public class RemoteObjectCliente extends UnicastRemoteObject implements
		RemoteInterfaceCliente {

	private static final long serialVersionUID = 1L;
	private static HibernateDAOCliente hbtDAOCliente;

	protected RemoteObjectCliente() throws RemoteException {
		super();
		hbtDAOCliente = HibernateDAOCliente.getInstancia();
	}

	private Cliente ClienteToEntity(ClienteDTO clienteDTO) {
		return new Cliente(clienteDTO.getIdCliente(), clienteDTO.getNombre());
	}

	public void altaCliente(ClienteDTO clienteDto) throws RemoteException {
		Cliente cliente = new Cliente();
		cliente = ClienteToEntity(clienteDto);
		hbtDAOCliente.guardar(cliente);
	}

	public void bajaCliente(int id) throws RemoteException {
		// TODO Auto-generated method stub
	}

	public void modificarCliente(ClienteDTO clienteDto) throws RemoteException {
		// TODO Auto-generated method stub
	}

	public ClienteDTO obtenerClientePorID(int id) throws RemoteException {
		return hbtDAOCliente.obtenerClientePorID(id);
	}

	public List<ClienteDTO> obtenerClientes() throws RemoteException {
		return hbtDAOCliente.obtenerClientes();
	}

}
