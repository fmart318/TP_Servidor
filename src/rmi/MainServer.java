package rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;

import dto.VehiculoDTO;

public class MainServer extends RmiStarter {

	public MainServer() {
		super(MainServer.class);
	}

	@Override
	public void doCustomRmiHandling() {
		try {
			// RemoteObject remoteObject = new RemoteObject();
			// LocateRegistry.createRegistry(1099);
			// Naming.rebind("//localhost/tpo", remoteObject);
			// System.out.println("Fijado en //localhost/tpo");

			RemoteObjectCliente remoteObjectCliente = new RemoteObjectCliente();
			LocateRegistry.createRegistry(1099);
			Naming.rebind("remoteInterfaceCliente", remoteObjectCliente);
			System.out.println("Fijado en remoteInterfaceCliente");
			
			RemoteObjectProveedor remoteObjectProveedor = new RemoteObjectProveedor();
			Naming.rebind("remoteInterfaceProveedor", remoteObjectProveedor);
			System.out.println("Fijado en remoteInterfaceProveedor");
			
			RemoteInterfaceVehiculo remoteInterfaceVehiculo = new RemoteObjectVehiculo();
			Naming.rebind("remoteInterfaceProveedor", remoteInterfaceVehiculo);
			System.out.println("Fijado en remoteInterfaceVehiculo");

		} catch (Exception e) {

			System.err.println("Remote Object Exception");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws RemoteException {
		new MainServer();
	}

}
