package rmi;

import java.util.ArrayList;
import java.util.List;

import dto.CargaDTO;
import dto.ClienteDTO;
import dto.DireccionDTO;
import dto.EmpresaDTO;
import dto.EnvioDTO;
import dto.FacturaDTO;
import dto.HabilitadoDTO;
import dto.ParticularDTO;
import dto.PedidoDTO;
import dto.PlanDeMantenimientoDTO;
import dto.PrecioVehiculoDTO;
import dto.ProductoDTO;
import dto.ProveedorDTO;
import dto.RemitoDTO;
import dto.RutaDTO;
import dto.SucursalDTO;
import dto.TransporteDTO;
import dto.TrayectoDTO;
import dto.VehiculoDTO;
import dto.ViajeDTO;
import entities.Carga;
import entities.Cliente;
import entities.Direccion;
import entities.Empresa;
import entities.Envio;
import entities.Factura;
import entities.Habilitado;
import entities.Particular;
import entities.Pedido;
import entities.PlanDeMantenimiento;
import entities.PrecioVehiculo;
import entities.Producto;
import entities.Proveedor;
import entities.Remito;
import entities.Ruta;
import entities.Sucursal;
import entities.Transporte;
import entities.Trayecto;
import entities.Vehiculo;
import entities.Viaje;

public class EntityManager {
	
	public Carga cargaToEntity(CargaDTO cargaDTO) {
		return new Carga(cargaDTO.getIdCarga(), cargaDTO.getPeso(), cargaDTO.getAncho(), cargaDTO.getAlto(),
				cargaDTO.getProfundidad(), cargaDTO.getVolumen(), cargaDTO.getFragilidad(), cargaDTO.getTratamiento(),
				cargaDTO.getApilable(), cargaDTO.isRefrigerable(), cargaDTO.getCondiciones(), cargaDTO.isDespachado(),
				cargaDTO.getTipoMercaderia());
	}

	public Cliente clienteToEntity(ClienteDTO clienteDTO) {
		return new Cliente(clienteDTO.getIdCliente(), clienteDTO.getNombre());
	}

	public Direccion direccionToEntity(DireccionDTO direccionDTO) {
		return new Direccion(direccionDTO.getIdDireccion(), direccionDTO.getCalle(), direccionDTO.getNumero(),
				direccionDTO.getPiso(), direccionDTO.getDepartamento(), direccionDTO.getCP());
	}

	public Empresa empresaToEntity(EmpresaDTO empresaDTO) {
		List<Producto> productos = new ArrayList<Producto>();
		for (ProductoDTO producto : empresaDTO.getProductos())
			productos.add(productoToEntity(producto));
		return new Empresa(empresaDTO.getIdCliente(), empresaDTO.getNombre(), empresaDTO.getCUIT(),
				empresaDTO.getTipo(), empresaDTO.getDetallePoliticas(), productos,
				empresaDTO.getSaldoCuentaCorriente());
	}

	public Envio envioToEntity(EnvioDTO envioDTO) {
		Envio e = new Envio(envioDTO.getIdEnvio(), envioDTO.getFechaSalida(), envioDTO.getFechaLlegada(),
				envioDTO.isCumpleCondicionesCarga(), envioDTO.getEstado(), pedidoToEntity(envioDTO.getPedido()),
				envioDTO.getPrioridad());

		e.setSucursalOrigen(envioDTO.getSucursalOrigen());
		return e;
	}

	public Factura facturaToEntity(FacturaDTO facturaDTO) {
		return new Factura(facturaDTO.getIdFactura(), pedidoToEntity(facturaDTO.getPedido()), facturaDTO.getPrecio());
	}

	public Habilitado habilitadoToEntity(HabilitadoDTO habilitadoDTO) {
		return new Habilitado(habilitadoDTO.getNombre(), habilitadoDTO.getDniHabilitado());
	}

	public Particular particularToEntity(ParticularDTO particularDTO) {
		List<Habilitado> habilitados = new ArrayList<Habilitado>();
		for (HabilitadoDTO habilitado : particularDTO.getHabilitados())
			habilitados.add(habilitadoToEntity(habilitado));
		return new Particular(particularDTO.getIdCliente(), particularDTO.getNombre(), particularDTO.getDNI(),
				particularDTO.getApellido(), habilitados);
	}

	public Pedido pedidoToEntity(PedidoDTO pedidoDTO) {
		List<Carga> cargas = new ArrayList<Carga>();
		for (CargaDTO cargaDTO : pedidoDTO.getCargas())
			cargas.add(cargaToEntity(cargaDTO));

		Pedido p = new Pedido(pedidoDTO.getIdPedido(), direccionToEntity(pedidoDTO.getDireccionCarga()),
				direccionToEntity(pedidoDTO.getDireccionDestino()), pedidoDTO.getFechaCarga(),
				pedidoDTO.getHoraInicio(), pedidoDTO.getHoraFin(), pedidoDTO.getFechaMaxima(), cargas,
				pedidoDTO.getPrecio(), pedidoDTO.getSucursalDestino(), pedidoDTO.getSucursalOrigen(),
				pedidoDTO.isSolicitaTransporteDirecto(), pedidoDTO.isSolicitaAvionetaParticular(),
				clienteToEntity(pedidoDTO.getCliente()));
		p.setSucursalOrigen(pedidoDTO.getSucursalOrigen());
		return p;
	}

	public PlanDeMantenimiento planDeMantenimientoToEntity(PlanDeMantenimientoDTO planDeMantenimientoDTO) {
		return new PlanDeMantenimiento(planDeMantenimientoDTO.getIdPlanDeMantenimiento(),
				planDeMantenimientoDTO.getDiasProxControl(), planDeMantenimientoDTO.getDiasDemora(),
				planDeMantenimientoDTO.getKmProxControl());
	}

	public PrecioVehiculo precioVehiculoToEntity(PrecioVehiculoDTO precioVehiculoDTO) {
		return new PrecioVehiculo(precioVehiculoDTO.getIdPrecioVehiculo(), precioVehiculoDTO.getTipoVehiculo(),
				precioVehiculoDTO.getPrecio());
	}

	public Producto productoToEntity(ProductoDTO productoDTO) {
		return new Producto(productoDTO.getIdProducto(), productoDTO.getNombre(), productoDTO.getTipo());
	}

	public Proveedor proveedorToEntity(ProveedorDTO proveedorDTO) {
		return new Proveedor(proveedorDTO.getIdProveedor(), proveedorDTO.getCompania(),
				proveedorDTO.getTipoMercaderia());
	}

	public Remito remitoToEntity(RemitoDTO remitoDTO) {
		return new Remito(remitoDTO.getIdRemito(), pedidoToEntity(remitoDTO.getPedido()));
	}

	public Ruta rutaToEntity(RutaDTO rutaDTO) {
		List<Trayecto> trayectos = new ArrayList<Trayecto>();
		for (TrayectoDTO trayecto : rutaDTO.getTrayectos())
			trayectos.add(trayectoToEntity(trayecto));
		return new Ruta(rutaDTO.getIdRuta(), trayectos, rutaDTO.getPrecio());
	}

	public Sucursal sucursalToEntity(SucursalDTO sucursalDTO) {
		return new Sucursal(sucursalDTO.getIdSucursal(), sucursalDTO.getNombre(),
				direccionToEntity(sucursalDTO.getUbicacion()), null);
	}

	public Transporte transporteToEntity(TransporteDTO transporteDTO) {
		return new Transporte(transporteDTO.getIdProveedor(), transporteDTO.getCompania(),
				transporteDTO.getTipoMercaderia(), transporteDTO.getTipoTransporte());
	}

	public Trayecto trayectoToEntity(TrayectoDTO trayectoDTO) {
		return new Trayecto(trayectoDTO.getIdTrayecto(), sucursalToEntity(trayectoDTO.getSucursalOrigen()),
				sucursalToEntity(trayectoDTO.getSucursalDestino()), trayectoDTO.getTiempo(), trayectoDTO.getKm(),
				trayectoDTO.getPrecio());
	}

	public Vehiculo vehiculoToEntity(VehiculoDTO vehiculoDTO) {

		return new Vehiculo(vehiculoDTO.getIdVehiculo(), vehiculoDTO.getTipo(), vehiculoDTO.getVolumen(),
				vehiculoDTO.getPeso(), vehiculoDTO.getAncho(), vehiculoDTO.getAlto(), vehiculoDTO.getProfundidad(),
				vehiculoDTO.getTara(), vehiculoDTO.getKilometraje(), vehiculoDTO.getEstado(),
				vehiculoDTO.isEnGarantia(), vehiculoDTO.isTrabajoEspecifico(), vehiculoDTO.getFechaUltimoControl(),
				planDeMantenimientoToEntity(vehiculoDTO.getPlanDeMantenimiento()));

	}

	public Viaje viajeToEntity(ViajeDTO viajeDTO) {
		List<Envio> envios = new ArrayList<Envio>();
		for (EnvioDTO envioDTO : viajeDTO.getEnvios())
			envios.add(envioToEntity(envioDTO));
		return new Viaje(viajeDTO.getIdViaje(), envios, viajeDTO.getFechaLlegada(),
				sucursalToEntity(viajeDTO.getSucursalOrigen()), sucursalToEntity(viajeDTO.getSucursalDestino()),
				viajeDTO.isFinalizado(), vehiculoToEntity(viajeDTO.getVehiculo()));
	}

}
