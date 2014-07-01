/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metodosimplex;

import metodosimplex.Formulas.Edwin;

/**
 * @author Edwin
 */
public class Tabla {
    //indica el numero de variables existentes en la tabla
    private int NumVariables;
    //indica el numero de restricciones existentes "altura de la tabla"
    private int NumRestricciones;
    //indica el valor total de amplitud de la tabla, incluyendo variables adicionales y z
    private int ancho;
    //la tabla que va a ser somedida a los cambios correspondientes
    private double tabla[][];
    //vector que guarda los resultados a partir de la tabla
    protected double resultado[];
    //valor de z
    protected double z;
    //valor de la gran M, donde M = promedio lado derecho * mil
    private double M;
    //vector que indica que columna contiene la variable artificial
    private int[] casoIgual;//guarda un valor de uno para la fila que tenga =

    /*
     * CONSTRUCTOR DE LA CLASE TABLA
     */
    public Tabla(Problema problema) {
        calcularM(problema);
        NumRestricciones = problema.restricciones.size();
        casoIgual = new int[NumRestricciones];
        NumVariables = maximoNumSubindices(problema);
        ancho = NumVariables +variablesAdicionales(problema) + 1;
        tabla = new double[NumRestricciones][ancho];
        //msm(" Valor de la tabla al iniciar, alto: "+NumRestricciones +",ancho: "+ancho);
        int k = NumVariables;
        for (int i = 0; i < NumRestricciones; i++) {
            //incorporar subindices
            for (int ii = 0; ii < NumVariables; ii++) {
                try {
                    tabla[i][ii] = problema.restricciones.get(i).subindices[ii];
                } catch (Exception ex) {
                    tabla[i][ii] = 0;
                }
            }
            //incorporar variables de holgura
            if (problema.restricciones.get(i).valorZ != 1)/*Excepto en la funcion objetivo*/ {
                if (variablePorSigno(problema.restricciones.get(i).desigualdad) == 1)/*en caso de <= o =*/ {
                    tabla[i][k] = 1;
                    if (problema.restricciones.get(i).desigualdad == 0)/*en igual tambien se añade M*/ {
                        tabla[0][k] = M;
                        casoIgual[i] = 1;
                    }
                    k += 1;
                }
                if (variablePorSigno(problema.restricciones.get(i).desigualdad) == 2)/*en caso de >=*/{
                    tabla[i][k] = -1;
                    tabla[i][k + 1] = 1;
                    k += 2;
                }
            }
            //incorporar resultado
            tabla[i][ancho - 2] = problema.restricciones.get(i).solucion;
            //incorporar Z
            tabla[i][ancho - 1] = problema.restricciones.get(i).valorZ;
        }
        //tabla = Edwin.recortarMatriz(tabla, NumRestricciones, ancho);
    }

    /*
     * La prueba de optimidad verifica que no hayan valores negativos el la func obj
     */
    private boolean pruebaOptimidad() {
        boolean r = true;
        //para la prueba de optimidad todos los valores de la primera fila deben ser positivos
        for (int i = 0; i < tabla[0].length; i++) {
            if (tabla[0][i] < 0) {
                r = false;
                break;
            }
        }
        return r;
    }

    /*
     *Calcula la columna pivote a partir del menor
     */
    private int calcularColumnaPivote() {
        int indiceResult = 0;
        double menor = tabla[0][indiceResult];
        //se toman en cuenta solamente el area de las variables
        for (int i = 0; i < tabla[0].length - 2; i++) {
            //si hay un valor menor al anterior y este es menor que cero se lo considera col piv
            if (tabla[0][i] <= menor && tabla[0][i] < 0) {
                menor = tabla[0][i];
                indiceResult = i;
            }
        }
        return indiceResult;
    }

    /*
     * Calcula la fila pivote a partir de valores
     */
    private int calcularFilaPivote(int columnaPiv) {
        double valores[] = new double[NumRestricciones - 1];
        int indices[] = new int[NumRestricciones - 1];
        int cont = 0;
        for (int i = 0; i < NumRestricciones - 1; i++) {
            if (tabla[i + 1][columnaPiv] > 0 && tabla[i + 1][tabla[0].length - 2] != 0) {

                indices[cont] = i + 1;
                valores[cont] = Math.abs(tabla[i + 1][tabla[0].length - 2] / tabla[i + 1][columnaPiv]);
                msm(" " + Double.toString(tabla[i + 1][tabla[0].length - 2]) + " / " + Double.toString(tabla[i + 1][columnaPiv]) + " = " + Double.toString(valores[cont]));
                cont += 1;
            }
        }
        double menor = valores[0];
        int filaPivote = 1;
        for (int i = 0; i < cont; i++) {
            if (valores[i] <= menor && valores[i] != 0) {
                menor = valores[i];
                filaPivote = indices[i];
            }
        }
        System.out.println("fila pivote = " + filaPivote);
        return filaPivote;

    }

    /*
     * Simplifica unicamente la fila pivote para hacer 1 al numero pivote
     */
    private void simplificarFilaPivote(int filaPivote, double numeroPivote) {
        for (int i = 0; i < tabla[0].length; i++) {
            tabla[filaPivote][i] = tabla[filaPivote][i] / numeroPivote;
        }
    }

    /*
     * Simplifica el resto de las filas considerando la fila pivote
     */
    private void simplificarTodoFilaPivote(int filaPivote, int columnaPivote) {
        for (int i = 0; i < tabla.length; i++) {
            if (tabla[i][columnaPivote] != 0 && i != filaPivote) {
                if (tabla[i][columnaPivote] > 0) {
                    //positivo
                    double factor = tabla[i][columnaPivote] * -1;
                    for (int j = 0; j < tabla[0].length; j++) {
                        tabla[i][j] = tabla[filaPivote][j] * factor + tabla[i][j];
                    }
                } else {
                    //negativo
                    double factor = tabla[i][columnaPivote] * -1;
                    for (int j = 0; j < tabla[0].length; j++) {
                        tabla[i][j] = tabla[filaPivote][j] * factor + tabla[i][j];
                    }
                }
            }
        }
    }

    /*
     * Establece los valores resultados en la tabla
     */
    private void establecerResultado() {
        System.out.println(" NumVariables = " + NumVariables);
        resultado = new double[NumVariables];
        int k=0;
        for (int i = 0; i < tabla.length; i++) {
            for (int j = 0; j < NumVariables; j++) {
                if (tabla[i][j] == 1) {
                    resultado[j] = tabla[i][tabla[0].length - 2];
                    k=j;
                }
            }
        }
        z=tabla[0][tabla[0].length - 2];
        System.out.println("");
        for (int i = 0; i < resultado.length; i++) {
            System.out.println(" x" + (i + 1) + " = " + resultado[i]);
        }
    }

    /*
     * Resulve el método simplex modificando la tabla del presente objeto
     */
    public void resolverMetodoSimplex(boolean accionMax) {
        int iteracion = 1; //utilizado para comprobacion
             /*
         * 1. prueba de optimidad. la matriz es optima si no hay valores negativos
         * en la funcion objetivo, caso contrario empezar por la de menor valor
         */

        //LA GRAN M ///////////
        //solo aparece cuando caso igual tiene valores de 1
        try {
            double[] restador = null;
            for (int i = 0; i < casoIgual.length; i++) {
                if (casoIgual[i] == 1) {
                    //encuentra la fila que se multiplica para M
                    System.out.println("hay = en la fila " + i);
                    restador = Edwin.obtenerValores(tabla[i]);

                    //System.arraycopy(tabla[i],0,restador,0,tabla[i].length);
                    //determina el vector a restar
                    for (int k = 0; k < restador.length; k++) {
                        restador[k] *= M;
                        tabla[0][k] -= restador[k];
                    }
                    casoIgual[i] = 0;
                    imprimirTabla("LA GRAN M en iteracion " + iteracion);
                    iteracion += 1;
                }

            }
        } catch (Exception ex1) {
            System.out.println("Error de copia");
            System.out.println(ex1.getCause());
        }

        ////caso todos <=
        msm("Ingresando al while de optimidad");

        boolean tieneRespuesta=true;
        while (pruebaOptimidad() != true && tieneRespuesta==true ) {

            //Se calcula los valores de columna pivote, fila pivote y numero pivote
            int columnaPivote = calcularColumnaPivote();
            int filaPivote = calcularFilaPivote(columnaPivote);
            double numeroPivote = tabla[filaPivote][columnaPivote];
            msm("numero pivote: " + numeroPivote + " en la posicion[" + filaPivote + "][" + columnaPivote + "]" + " en iteracion " + iteracion);

            //se simplifica la fila pivote
            simplificarFilaPivote(filaPivote, numeroPivote);
            imprimirTabla("simplificado fila pivote en iteracion " + iteracion);

            //se simplifican las filas aledañas a la fila pivote
            simplificarTodoFilaPivote(filaPivote, columnaPivote);
            imprimirTabla("Simplificado completo en iteracion " + iteracion);
            iteracion += 1;
            if(iteracion==100){
                tieneRespuesta=false;
                String s="El problema no tiene solucion! "
                        + "\nRevise las ecuaciones en busqueda de incoherencias "
                        + "que hayan podido causar un bucle infinito";
                mensaje.show(s);
               
            }
        }
        ///////////////////////resuelve el problema de método simplex
        imprimirTabla(" TABLA RESULTADO SIMPLEX");
        establecerResultado();
        //} catch (Exception e) {System.out.println(" No hay restricciones");}
    }

    /*
     * CALCULO de M PARA EL CASO DE =
     */
    private void calcularM(Problema problema) {
        double suma = 0;
        for (int i = 1; i < problema.restricciones.size(); i++) {
            suma += problema.restricciones.get(i).solucion;
        }
        M = suma / (problema.restricciones.size() - 1) * 1000;
    }

    /*
     * Encuentra la cantidad maxima de subindices
     */
    private int maximoNumSubindices(Problema problema) {
        int maximo = 0;
        for (int i = 0; i < problema.restricciones.size(); i++) {
            if (problema.restricciones.get(i).subindices.length > maximo) {
                maximo = problema.restricciones.get(i).subindices.length;
            }
        }
        return maximo;
    }
    /*
     * Permite calcular cuantas variables deben añadirse por un signo
     */

    private int variablePorSigno(int desigualdad) {
        int result = 0;
        if (desigualdad == 1) {
            result = 2;
        }
        if (desigualdad == 0 || desigualdad == -1) {
            result = 1;
        }

        return result;
    }

    /*
     * Permite calcular cuantas variables se van a añadir
     */
    private int variablesAdicionales(Problema problema) {
        int amplitud = 0;
        for (int i = 0; i < problema.restricciones.size(); i++) {
            amplitud += variablePorSigno(problema.restricciones.get(i).desigualdad);
        }
        return amplitud;
    }

    /*
     * Permite ver el resultado de la tabla en consola
     */
    public void imprimirTabla(String titulo) {
        System.out.println("");
        System.out.println("-->  " + titulo);
        for (int ii = 0; ii < tabla.length; ii++) {
            System.out.println(" ");
            for (int jj = 0; jj < tabla[0].length; jj++) {
                System.out.print(Double.toString(tabla[ii][jj]) + "  ,  ");
            }
        }
        System.out.println("");
    }

    private void msm(String mensaje) {
        System.out.println("::::::::::Informacion::::::::");
        System.out.println("Edwin dice: --> " + mensaje);
        System.out.println("");
    }
}
