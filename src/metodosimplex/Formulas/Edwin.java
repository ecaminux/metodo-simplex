/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metodosimplex.Formulas;

/**
 *
 * @author Edwin
 */
public class Edwin {

    private static double terminosAuxiliar[] = new double[15];
    private static double terminos[];
    static int indMax = 0;  //indice maximo en la entrada de datos


    /*
     * OBTENER VALORES
     */
    public static double[] obtenerValores(double [] entrada){
        double[] resultado=new double[entrada.length];
        for(int i=0;i<entrada.length;i++){
            resultado[i]=entrada[i];
        }
        return resultado;
    }


    /*
     * Permite recontar un arreglo al valor de indice descrito
     */
    public static double[] recortarArreglo(double[] entrada, int indice) {
        double ayuda[] = new double[indice];
        for (int i = 0; i < indice; i++) {
            ayuda[i] = entrada[i];
        }
        return ayuda;
    }

    /*
     * Permite recortar una matriz al valor de indices descritos
     */
    public static double[][] recortarMatriz(double[][] entrada, int indicex, int indicey) {
        double ayuda[][] = new double[indicex][indicey];
        for (int i = 0; i < indicex; i++) {
            for (int ii=0;ii<indicey;ii++){
            ayuda[i][ii] = entrada[i][ii];
            }
        }
        return ayuda;
    }

    //SECCION DE CAPTURA DE CADENAS DE CARACTERES
    static int indice = 0;

    /*
     * Permite capturar los valores de una serie de caracteres
    que simbolizan un conjunto de término
     */
    public static double[] capturar(String entrada) {
        for (int n = 0; n < terminosAuxiliar.length; n++) {
            terminosAuxiliar[n] = 0;
        }
        indMax = 0;
        String termino = "";
        indice = 0;
        //insercion del punto final
        entrada = entrada + ":";
        while (entrada.length() - 1 > 0) {

            indice = siguienteSigno(entrada);
            termino = entrada.substring(0, indice);
            entrada = entrada.substring(indice);
            separar(termino);
        }
        terminos = new double[indMax];
        System.arraycopy(terminosAuxiliar, 0, terminos, 0, indMax);
        return terminos;
    }

    /**
     * Permite encontrar el indice del siguiente signo
     * @param entrada: ecuacion en juego
     * @return n: indice del signo o inicio del siguiente termino
     */
    private static int siguienteSigno(String entrada) {
        int nn = 0;
        for (int i = 1; i <= entrada.length() - 1; i++) {
            if (entrada.charAt(i) == '+' || entrada.charAt(i) == '-' || entrada.charAt(i) == ':') {
                nn = i;
                i = entrada.length();
            }
        }
        return nn;
    }

    /**
     * Permite separar en Subindice y exponente a un mismo termino
     * @param termino: termino de la ecuacion
     */
    private static void separar(String termino) {
        int ind = 0;
        try {
            if (termino.charAt(0) == '+') {
                termino = termino.substring(1);
            }
            if (termino.charAt(0) == 'x') {
                termino = "1" + termino;
            }
            if (termino.charAt(0) == '-' && termino.charAt(1) == 'x') {
                termino = "-1" + termino.substring(1);
            }
        } catch (Exception ex) {
            System.out.println("ERROR DE TERMINO en separar()");
        }

        //2x1
        if (termino.contains("x")) {
            ind = Integer.parseInt(termino.substring(termino.indexOf('x') + 1));
            terminosAuxiliar[ind - 1] = Double.parseDouble(termino.substring(0, termino.indexOf('x')));
        }
        if (ind > indMax) {
            indMax = ind;
        }
    }
}
