package rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

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
//			LocateRegistry.createRegistry(1098);
			Naming.rebind("remoteInterfaceProveedor", remoteObjectProveedor);
			System.out.println("Fijado en remoteInterfaceProveedor");

		} catch (Exception e) {

			System.err.println("Remote Object Exception");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws RemoteException {
		new MainServer();
	}

}
