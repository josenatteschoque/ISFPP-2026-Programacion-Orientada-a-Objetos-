package datos;

public class DAOLoader {

	private static DAOFactory fabrica;

	public static DAOFactory getFactory() {
		if (fabrica == null) {
			fabrica = new TxtDAOFactory(new Configuracion());
		}
		return fabrica;
	}
}