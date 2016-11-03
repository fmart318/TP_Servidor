package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import dao.HibernateDAOCliente;
import dao.HibernateDAOProveedor;
import dto.ClienteDTO;
import dto.ProveedorDTO;
import entities.Cliente;
import entities.Proveedor;

public class RemoteObjectProveedor extends UnicastRemoteObject implements RemoteInterfaceProveedor {

	private static final long serialVersionUID = 1L;
	private static HibernateDAOProveedor hbtDAOProveedor;

	protected RemoteObjectProveedor() throws RemoteException {
		super();
		hbtDAOProveedor = HibernateDAOProveedor.getInstancia();
	}


	public List<ProveedorDTO> obtenerProveedores() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public ProveedorDTO obtenerProveedor(int idProveedor) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void altaProveedor(ProveedorDTO proveedorDTO) throws RemoteException {
		Proveedor proveedor = new Proveedor();
		proveedor = ProveedorToEntity(proveedorDTO);
		hbtDAOProveedor.guardar(proveedor);
	}
	
	private Proveedor ProveedorToEntity(ProveedorDTO proveedorDTO) {
		return new Proveedor(proveedorDTO.getIdProveedor(),
				proveedorDTO.getCompania(), proveedorDTO.getTipoMercaderia());
	}


	public void eliminarProveedor(int idProveedor) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void modificarProveedor(ProveedorDTO nuevoProveedor) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
