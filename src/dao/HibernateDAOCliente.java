package dao;

import hbt.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

import dto.ClienteDTO;
import dto.ParticularDTO;
import dto.PedidoDTO;
import dto.ViajeDTO;
import entities.Cliente;
import entities.Particular;
import entities.Pedido;
import entities.Viaje;

public class HibernateDAOCliente extends HibernateDAO {

	private static HibernateDAOCliente instancia;

	public static HibernateDAOCliente getInstancia() {
		if (instancia == null) {
			sessionFactory = HibernateUtil.getSessionfactory();
			instancia = new HibernateDAOCliente();
		}
		return instancia;
	}

	@SuppressWarnings("unchecked")
	public List<ClienteDTO> obtenerClientes() {
		List<ClienteDTO> clientesDTO = new ArrayList<ClienteDTO>();
		try {
			List<Cliente> clientes = sessionFactory.openSession().createQuery("FROM Cliente").list();
			for (Cliente c : clientes) {
				clientesDTO.add(c.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return clientesDTO;
	}
	
	public ClienteDTO obtenerClientePorDNI(int dni) {

		ClienteDTO dto = new ClienteDTO();
		try {
			Cliente cliente = obtenerClienteEntityPorDNI(dni);
			dto = cliente.toDTO();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return dto;
	}
	
	private Cliente obtenerClienteEntityPorDNI(int dni) {
		Cliente cliente = (Cliente) sessionFactory.openSession().createQuery("FROM Cliente c where c.id=:id")
				.setParameter("id", dni).uniqueResult(); 
		return cliente;
	}

	public ClienteDTO obtenerClientePorID(int id) {
		ClienteDTO cl = new ClienteDTO();
		try {
			Cliente c = (Cliente) sessionFactory.openSession().createQuery("FROM Cliente c where c.id=:id")
					.setParameter("id", id).uniqueResult();
			cl = c.toDTO();
		} catch (Exception e) {

			System.out.println(e);

		}
		this.closeSession();
		return cl;
	}
	
	/* Tested and Passed */
	public ParticularDTO obtenerClienteParticular(int DNI) {
		ParticularDTO particularDTO = new ParticularDTO();
		try {
			Particular particular = (Particular) sessionFactory.openSession()
					.createQuery("FROM Particular p where p.DNI=:dni")
					.setParameter("dni", DNI).uniqueResult();
			particularDTO = particular.toDTO();
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return particularDTO;
	}
	
	/* Tested and Passed */
	@SuppressWarnings("unchecked")
	public List<ViajeDTO> obtenerViajesDeCliente(int idCliente) {
		List<ViajeDTO> viajesDTO = new ArrayList<ViajeDTO>();
		try {
			List<Viaje> viajes = sessionFactory.openSession()
					.createQuery("From Viaje v Join Envio e where e.idViaje=v.idViaje And e.pedido.cliente.idCliente=:id ")
					.setParameter("id", idCliente).list();
			for (Viaje viaje : viajes) {
				viajesDTO.add(viaje.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return viajesDTO;
	}
	
	@SuppressWarnings("unchecked")
	public List<PedidoDTO> obtenerPedidosDeCliente(int idCliente) {
		List<PedidoDTO> pedidosDTO = new ArrayList<PedidoDTO>();
		try {
			List<Pedido> pedidos = sessionFactory.openSession()
					.createQuery(
							"from Pedido p where p.cliente.idCliente =:id ")
					.setParameter("id", idCliente).list();
			for (Pedido pedido : pedidos) {
				pedidosDTO.add(pedido.toDTO());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.closeSession();
		return pedidosDTO;
	}
	
	public void eliminarCliente() {
		//TODO chequear que no tenga relaciones antes de eliminar
	}
}
