package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Strategy.PoliticaEspecificidad;
import Strategy.PoliticaGarantia;
import Strategy.PoliticaGeneral;
import Strategy.PoliticaMantenimiento;
import dao.HibernateDAOVehiculo;
import dto.PlanDeMantenimientoDTO;
import dto.VehiculoDTO;
import entities.PlanDeMantenimiento;
import entities.Vehiculo;

public class RemoteObjectVehiculo extends UnicastRemoteObject implements RemoteInterfaceVehiculo {

	private static final long serialVersionUID = 1L;
	private static HibernateDAOVehiculo hbtDAOVehiculo;
	private PoliticaMantenimiento politicaMantenimiento;

	protected RemoteObjectVehiculo() throws RemoteException {
		super();
		hbtDAOVehiculo = HibernateDAOVehiculo.getInstancia();
	}

	private Vehiculo VehiculoToEntity(VehiculoDTO vehiculoDTO) {

		return new Vehiculo(vehiculoDTO.getIdVehiculo(), vehiculoDTO.getTipo(), vehiculoDTO.getVolumen(),
				vehiculoDTO.getPeso(), vehiculoDTO.getAncho(), vehiculoDTO.getAlto(), vehiculoDTO.getProfundidad(),
				vehiculoDTO.getTara(), vehiculoDTO.getKilometraje(), vehiculoDTO.getEstado(),
				vehiculoDTO.isEnGarantia(), vehiculoDTO.isTrabajoEspecifico(), vehiculoDTO.getFechaUltimoControl(),
				PlanDeMantenimientoToEntity(vehiculoDTO.getPlanDeMantenimiento()));

	}

	private PlanDeMantenimiento PlanDeMantenimientoToEntity(PlanDeMantenimientoDTO planDeMantenimientoDTO) {
		return new PlanDeMantenimiento(planDeMantenimientoDTO.getIdPlanDeMantenimiento(),
				planDeMantenimientoDTO.getDiasProxControl(), planDeMantenimientoDTO.getDiasDemora(),
				planDeMantenimientoDTO.getKmProxControl());
	}

	public void altaVehiculo(VehiculoDTO vehiculoDTO) {
		hbtDAOVehiculo.guardar(VehiculoToEntity(vehiculoDTO));
	}

	public List<VehiculoDTO> obtenerVehiculos() {
		return hbtDAOVehiculo.obtenerVehiculos();
	}

	public boolean ControlarVehiculo(VehiculoDTO vehiculoDTO) {
		/*
		 * Obtengo la fecha en la que deberian hacerle el mantenimiento al
		 * vehiculo
		 */
		Calendar c = Calendar.getInstance();
		c.setTime(vehiculoDTO.getFechaUltimoControl());
		c.add(Calendar.DATE, vehiculoDTO.getPlanDeMantenimiento().getDiasProxControl());
		Date fecha = c.getTime();

		boolean Boolean = false;
		if (vehiculoDTO.getEstado().equals("En Deposito")) {
			System.out.println(vehiculoDTO.getKilometraje() + vehiculoDTO.getPlanDeMantenimiento().getKmProxControl());
			if (vehiculoDTO.getKilometraje() >= vehiculoDTO.getPlanDeMantenimiento().getKmProxControl()) {
				Boolean = true;
				if (vehiculoDTO.isEnGarantia()) {
					politicaMantenimiento = new PoliticaGarantia();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				} else if (vehiculoDTO.isTrabajoEspecifico()) {
					politicaMantenimiento = new PoliticaEspecificidad();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				} else {
					politicaMantenimiento = new PoliticaGeneral();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				}
				vehiculoDTO.getPlanDeMantenimiento().setKmProxControl(vehiculoDTO.getKilometraje() + 200);
				vehiculoDTO.getPlanDeMantenimiento().setDiasProxControl(60);
				hbtDAOVehiculo.modificar(VehiculoToEntity(vehiculoDTO));
			}
			/*
			 * Me fijo si la fecha calculada al principio esta antes que la de
			 * hoy, si lo esta, mando mantenimiento
			 */
			else if (fecha.before(new Date())) {
				Boolean = true;
				if (vehiculoDTO.isEnGarantia()) {
					politicaMantenimiento = new PoliticaGarantia();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				} else if (vehiculoDTO.isTrabajoEspecifico()) {
					politicaMantenimiento = new PoliticaEspecificidad();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				} else {
					politicaMantenimiento = new PoliticaGeneral();
					politicaMantenimiento.mandarAMantenimiento(vehiculoDTO);
				}
				vehiculoDTO.getPlanDeMantenimiento().setKmProxControl(vehiculoDTO.getKilometraje() + 200);
				vehiculoDTO.getPlanDeMantenimiento().setDiasProxControl(60);
				hbtDAOVehiculo.modificar(VehiculoToEntity(vehiculoDTO));
			}
		}
		return Boolean;
	}

	public VehiculoDTO obtenerVehiculo(VehiculoDTO v) {
		return hbtDAOVehiculo.obtenerVehiculo(v.getIdVehiculo());
	}

	public List<VehiculoDTO> obtenerVehiculosListos() {
		List<VehiculoDTO> aux = new ArrayList<VehiculoDTO>();
		List<VehiculoDTO> vehiculos = hbtDAOVehiculo.obtenerVehiculos();
		for (VehiculoDTO vehiculo : vehiculos) {
			if (vehiculo.getEstado().equals("En Deposito"))
				aux.add(vehiculo);
		}
		return aux;
	}

}
