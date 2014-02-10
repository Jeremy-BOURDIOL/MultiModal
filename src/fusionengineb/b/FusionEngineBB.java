/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fusionengineb.b;

import fr.dgac.ivy.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bourdije
 */
public class FusionEngineBB {
    private static final Logger LOG = Logger.getLogger(FusionEngineBB.class.getName());
    private final Ivy bus;
    private String lastSelected;
    private ArrayList<ArrayList<Point2D>> traces;
    
    private String x = "0";
    private String y = "0";
    private String obj = "";
    
    public FusionEngineBB() {
        traces = new ArrayList<>();
        bus = new Ivy("FusionEngine","FusionEngine Ready",null);
        try {
            bus.bindMsg("^ICAR (.*)",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    try {
                        bus.sendMsg("Palette:"+parsecmdICAR(args[0]));
                    } catch (IvyException ex) {
                        Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            bus.bindMsg("^sra5 Text=(.*) (.*)",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    try {
                        bus.sendMsg("Palette:"+parsecmdSRA(args[0]));
                    } catch (IvyException ex) {
                        Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            bus.bindMsg("^Palette:ResultatTesterPoint x=([0-9]*) y=([0-9]*) nom=(.*)",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    lastSelected = args[2];
                }
            });
            bus.bindMsg("^Palette:MouseReleased x=([0-9]*) y=([0-9]*)",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    try {
                        //bus.sendMsg("J'ai :"+args[0] + " : "+ args[1]);
                        x = ""+(Integer.parseInt(args[0])-40);
                        y = ""+(Integer.parseInt(args[1])-20);
                        bus.sendMsg("Palette:TesterPoint x="+args[0]+" y="+args[1]);
                    } catch (IvyException ex) {
                        Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            bus.start(null);
        } catch (IvyException ex) {
            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    private String parsecmdICAR(String string) {
        String rep = "";
        switch(string) {
            case "Rectangle" :
                rep = "CreerRectangle x="+x+" y="+y+" longueur=80 hauteur=40 couleurFond=white";
                break;
            case "Ellipse" :
                rep = "CreerEllipse x="+x+" y="+y;
                break;
            case "Haut" :
                rep = "DeplacerObjet nom="+lastSelected+" x=0 y=-25";
                break;
            case "Bas" :
                rep = "DeplacerObjet nom="+lastSelected+" x=0 y=25";
                break;
            case "Gauche" :
                rep = "DeplacerObjet nom="+lastSelected+" x=-25 y=0";
                break;
            case "Droite" :
                rep = "DeplacerObjet nom="+lastSelected+" x=25 y=0";
                break;
        }
        return rep;
    }
    

    private String parsecmdSRA(String string) {
        String rep = "";
        String cmd = skip3suspension(string);
        switch(cmd) {
            case "rectangle" :
                rep = "CreerRectangle x="+x+" y="+y+" longueur=80 hauteur=40 couleurFond=white";
                break;
            case "ellipse" :
                rep = "CreerEllipse x="+x+" y="+y;
                break;
            case "haut" :
                rep = "DeplacerObjet nom="+lastSelected+" x=0 y=-25";
                break;
            case "bas" :
                rep = "DeplacerObjet nom="+lastSelected+" x=0 y=25";
                break;
            case "gauche" :
                rep = "DeplacerObjet nom="+lastSelected+" x=-25 y=0";
                break;
            case "droite" :
                rep = "DeplacerObjet nom="+lastSelected+" x=25 y=0";
                break;
        }
        return rep;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new FusionEngineBB();
    }

    private String skip3suspension(String string) {
        String ret = string.replace("...", "");
        return ret;
    }
}
