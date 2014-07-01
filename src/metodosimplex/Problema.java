/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metodosimplex;
import java.awt.List;
import java.util.ArrayList;

/**
 *
 * @author Edwin
 */
public class Problema {
   Tabla proceso;
   boolean  accionMax; //maximizar = true , minimizar = false
   ArrayList<Restriccion> restricciones = new ArrayList<Restriccion>();

   public Problema(boolean max){
       accionMax=max;
   }

    public void nuevaRestriccion(String subindicesn,String desigualdadn,String valorObjn){
        try{
        restricciones.add(new Restriccion(subindicesn, desigualdadn, valorObjn,0));
        }catch(Exception ex){System.out.println("Problema en la restriccion "+restricciones.size());}
        }

    public void setFuncionObjetivo(double[] funcObj){
         for(int i=0;i<funcObj.length;i++){
             try{
            funcObj[i] = funcObj[i] * -1;
             }catch(Exception ex){
             System.out.println("Error en Problema.setFuncionObjetivo");
             }
         }
        restricciones.add(new Restriccion(funcObj, 0, 0, 1));
    }

/*
 * prepara el problema
 */
    public void preparar(){
        proceso=new Tabla(this);
        proceso.imprimirTabla("FASE DE PREPARACION");
    }

    public void resolverMetodoSimplex(List lista) {
        proceso.resolverMetodoSimplex(accionMax);
        //mensaje("numero de resultados . "+proceso.resultado.length);
        for(int i=0;i<proceso.resultado.length;i++){
                lista.add(" x"+(i+1)+" = "+proceso.resultado[i]);
        }
        lista.add(" z = "+proceso.z);
    }

    public void borrarTodo(){
     restricciones.clear();
     mensaje("Se ha borrado el problema");
    }

    public void imprimirTodo(){
          System.out.println("Funcion objetivo");
             System.out.println("Restricciones");
        for (int i=0;i<restricciones.size();i++){
            System.out.println("    restriccion "+(i+1));
            System.out.println("  subindices");
            for(int j=0;j<restricciones.get(i).subindices.length;j++){
              System.out.print(restricciones.get(i).subindices[j]+"  ,  ");
            }
            System.out.println("    desigualdad");
            System.out.println(restricciones.get(i).desigualdad);
            System.out.println("    valor");
            System.out.println(restricciones.get(i).solucion);
        }


    }

    private void mensaje(String msm){
        System.out.println("::::::::::Informacion:::::::: ");
        System.out.println("Edwin dice: --> " + msm);
        System.out.println("");
    }


}


//        try{
//        int iteracion = 1; //utilizado para comprobacion
//
//        while (proceso.pruebaOptimidad() != true) {
//            //caso falso
//            //Se clacula los valores de columna pivote, fila pivote y numero pivote
//            int columnaPivote = proceso.calcularColumnaPivote();
//            int filaPivote = proceso.calcularFilaPivote(columnaPivote, this);
//            double numeroPivote = proceso.tabla[filaPivote][columnaPivote];
//            System.out.println("numero pivote: " + numeroPivote + " en iteracion" + iteracion);
//
//            //simplificar fila pivote
//            for (int i = 0; i < proceso.tabla[0].length; i++) {
//                proceso.tabla[filaPivote][i] = proceso.tabla[filaPivote][i] / numeroPivote;
//            }
//            proceso.imprimirEnConsola("simplificado fila pivote en iteracion " + iteracion);
//            //simplificar las demas filas
//
//            for (int i = 0; i < proceso.tabla.length; i++) {
//                if (proceso.tabla[i][columnaPivote] != 0 && i != filaPivote) {
//                    if (proceso.tabla[i][columnaPivote] > 0) {
//                        //positivo
//                        double factor = proceso.tabla[i][columnaPivote]*-1;
//                        for (int j = 0; j < proceso.tabla[0].length; j++) {
//                            proceso.tabla[i][j] = proceso.tabla[filaPivote][j] * factor + proceso.tabla[i][j];
//                        }
//                    } else {
//                        //negativo
//                        double factor = proceso.tabla[i][columnaPivote] * -1;
//                        for (int j = 0; j < proceso.tabla[0].length; j++) {
//                            proceso.tabla[i][j] = proceso.tabla[filaPivote][j] * factor + proceso.tabla[i][j];
//                        }
//                    }
//                }
//            }
//            proceso.imprimirEnConsola("Simplificado completo en iteracion " + iteracion);
//            //guardar primer valor encontrado
//            iteracion += 1;
//        }
//        ///////////////////////reuelve el problema de método simplex
//        proceso.imprimirEnConsola(" TABLA RESULTADO SIMPLEX");
//
//        resultado=new double[proceso.NumSubindices];
//        for(int i=0;i<proceso.tabla.length;i++){
//             for(int j=0;j<proceso.NumSubindices;j++){
//               if(proceso.tabla[i][j]==1){
//                   resultado[j]=proceso.tabla[i][proceso.tabla[0].length-2];
//               }
//            }
//            }
//
//
//            for(int i=0;i<proceso.NumSubindices;i++){
//               System.out.println(" x"+(i+1)+" = "+resultado[i]);
//            }
//        }catch(Exception e){System.out.println(" No hay restricciones");}