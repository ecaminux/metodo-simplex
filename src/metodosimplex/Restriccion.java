/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metodosimplex;
import metodosimplex.Formulas.Edwin;

/**
 *
 * @author Edwin
 */
public class Restriccion {
    protected double subindices[];
    protected int desigualdad;
    protected double valorZ;
    /*
     * Es el valor del lado derecho
     */
    double solucion;
    public Restriccion(double[] subindicesn,int desigualdadn,double valorObjn,double valZ){
        subindices=subindicesn;
        desigualdad=desigualdadn;
        solucion=valorObjn;
        valorZ=valZ;
         ladoDerechoPositivo();
    }
    public Restriccion(String subindicesn, String desigualdadn, String valorObj,double valZ){
        //seccion de subindices
        subindices=Edwin.capturar(subindicesn);
        ///SECCION DE SIMBOLO DE DESIGUALDAD
        if(desigualdadn.compareTo("<=") == 0){desigualdad = -1;}
        if(desigualdadn.compareTo(">=") == 0){desigualdad = 1;}
        if(desigualdadn.compareTo("=") == 0){desigualdad = 0;}
        //SECCION DE VALOR OBJETIVO
        solucion = Integer.parseInt(valorObj);
        valorZ=valZ;

        //Se verifica que el lado derecho sea valor positivo o 0
        ladoDerechoPositivo();
        }

    private void ladoDerechoPositivo(){
        if(solucion<0){
            solucion*=-1;
            desigualdad*=-1;
            for(int i=0;i<subindices.length;i++){
               if(subindices[i]!=0){
                    subindices[i]*=-1;
                }
            }
        }
    }

//    public void setValues(double[] subindicesn,int desigualdadn,double valorObjn,double valZ){
//        subindices=subindicesn;
//        desigualdad=desigualdadn;
//        solucion=valorObjn;
//        valorZ=valZ;
//    }
}
