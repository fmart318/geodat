package app.geodat.Util;


public class Formulario {
	
	int cuentas;
	int proyectos;
	String fecha;
	String locacion;
	String idPunto;
	String referencia;
	
	boolean pregunta1;
	boolean pregunta2;
	boolean pregunta3;
	String pregunta4;
	String pregunta5;
	String comentario;
	
	String codigo;
	
	boolean multi;
	String idcuenta;
	
	
	public Formulario(int cuentas, int proyectos, String fecha, String locacion, String idPunto,
			String referencia, Boolean pregunta1, Boolean pregunta2, Boolean pregunta3, String pregunta4,
			String pregunta5, String comentario, String codigo, boolean multi, String idCuenta) {

		this.cuentas = cuentas;
		this.proyectos = proyectos;
		this.fecha = fecha;
		this.locacion = locacion;
		this.idPunto = idPunto;
		this.referencia = referencia;
		this.pregunta1 = pregunta1;
		this.pregunta2 = pregunta2;
		this.pregunta3 = pregunta3;
		this.pregunta4 = pregunta4;
		this.pregunta5 = pregunta5;
		this.comentario = comentario;
		this.codigo = codigo;
		this.multi = multi;
		this.idcuenta = idCuenta;
	}


	public boolean isMulti() {
		return multi;
	}


	public void setMulti(boolean multi) {
		this.multi = multi;
	}


	public String getIdcuenta() {
		return idcuenta;
	}


	public void setIdcuenta(String idcuenta) {
		this.idcuenta = idcuenta;
	}


	public Formulario() {
		// TODO Auto-generated constructor stub
	}


	public int getCuentas() {
		return cuentas;
	}


	public void setCuentas(int cuentas) {
		this.cuentas = cuentas;
	}


	public int getProyectos() {
		return proyectos;
	}


	public void setProyectos(int proyectos) {
		this.proyectos = proyectos;
	}


	public String getFecha() {
		return fecha;
	}


	public void setFecha(String fecha) {
		this.fecha = fecha;
	}


	public String getLocacion() {
		return locacion;
	}


	public void setLocacion(String locacion) {
		this.locacion = locacion;
	}


	public String getIdPunto() {
		return idPunto;
	}


	public void setIdPunto(String idPunto) {
		this.idPunto = idPunto;
	}


	public String getReferencia() {
		return referencia;
	}


	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}


	public boolean isPregunta1() {
		return pregunta1;
	}


	public void setPregunta1(boolean pregunta1) {
		this.pregunta1 = pregunta1;
	}


	public boolean isPregunta2() {
		return pregunta2;
	}


	public void setPregunta2(boolean pregunta2) {
		this.pregunta2 = pregunta2;
	}


	public boolean isPregunta3() {
		return pregunta3;
	}


	public void setPregunta3(boolean pregunta3) {
		this.pregunta3 = pregunta3;
	}


	public String getPregunta4() {
		return pregunta4;
	}


	public void setPregunta4(String pregunta4) {
		this.pregunta4 = pregunta4;
	}


	public String getPregunta5() {
		return pregunta5;
	}


	public void setPregunta5(String pregunta5) {
		this.pregunta5 = pregunta5;
	}


	public String getComentario() {
		return comentario;
	}


	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	

}
