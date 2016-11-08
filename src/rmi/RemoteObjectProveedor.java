package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import dao.HibernateDAOProveedor;
import dto.ProveedorDTO;
import entities.Proveedor;

public class RemoteObjectProveedor extends UnicastRemoteObject implements RemoteInterfaceProveedor {

	private static final long serialVersionUID = 1L;
	private static HibernateDAOProveedor hbtDAOProveedor;

	protected RemoteObjectProveedor() throws RemoteException {
		super();
		hbtDAOProveedor = HibernateDAOProveedor.getInstancia();
	}


	public List<ProveedorDTO> obtenerProveedores() throws RemoteException {
		return hbtDAOProveedor.obtenerProveedores();
	}

	public ProveedorDTO obtenerProveedor(int idProveedor) throws RemoteException {
		return hbtDAOProveedor.obtenerProveedorPorId(idProveedor);
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
		hbtDAOProveedor.eliminarProveedor(idProveedor);
	}

	public void modificarProveedor(ProveedorDTO nuevoProveedor) throws RemoteException {
		hbtDAOProveedor.modificar(new Proveedor(nuevoProveedor.getIdProveedor(), nuevoProveedor.getCompania(), 
				nuevoProveedor.getTipoMercaderia()));
	}

}
