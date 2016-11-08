package Strategy;

import dto.VehiculoDTO;

public class PoliticaGarantia implements PoliticaMantenimiento{

	public void mandarAMantenimiento(VehiculoDTO vehiculoDTO) {
		vehiculoDTO.setEstado("En mantenimiento por garantia.");
	}

	
}
