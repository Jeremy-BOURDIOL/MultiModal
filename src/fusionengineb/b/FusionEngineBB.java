/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fusionengineb.b;

import fr.dgac.ivy.*;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
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
    private boolean waiting_loc = false;
    private ArrayList<ArrayList<Point2D>> traces;
    
    private String x = "0";
    private String y = "0";
    private String obj = "";
    private String x_last = "";
    private String y_last = "";

    private int x0;
    private int y0;
    private int x1;
    private int y1;
    public enum Etats{INIT,CREER,C_FORME,C_POSITION,C_COULEUR, C_ATTENTE_POSITION,DEPLACER,D_FORME, D_FORME_SELECTIONNEE, D_POSITION};
    public enum Forme{NULL,RECTANGLE,ELLIPSE};
    private Etats e = Etats.INIT;
    private Forme f;
    private String c = "white";
    private Timer t;
    private Timer t2;
    private TimerTask tc;
    private int xc = 0;
    private int yc = 0;
    private String idforme;
    
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
           
             bus.bindMsg("^sra5 Text=creer",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                        //bus.sendMsg("Palette:"+parsecmdSRA(args[0]));
                       switch (e) {
                            case INIT :
                                e = Etats.CREER;
                                System.out.println(e);
                                break;
                       }    
                    }
            });
            bus.bindMsg("^sra5 Text=deplacer ce",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    
                        switch (e) {
                            case INIT :
                                e = Etats.DEPLACER;
                                System.out.println(e);
                                break;
                       } 
                    
                }
            });
            bus.bindMsg("^sra5 Text=rectangle",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                        //bus.sendMsg("Palette:"+parsecmdSRA(args[0]));
                        switch(e){
                            case CREER :
                                e = Etats.C_FORME;
                                f = Forme.RECTANGLE;
                                System.out.println(e+" "+f);
                                tc = new TimerTask() {

                                    @Override
                                    public void run() {
                                        e = Etats.INIT;
                                        System.out.println(e);
                                        try {
                                            creerForme(f,xc,yc,c);
                                        } catch (IvyException ex) {
                                            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        xc = 0;
                                        yc = 0;
                                        c = "white";
                                        f = Forme.NULL;
                                    }
                                };
                                t = new Timer();
                                t.schedule(tc, 3000);
                                break;
                               case DEPLACER :
                                   e =Etats.D_FORME;
                                   System.out.println(e);
                                   
                                   break;
                        }
                    
                }
            });
            bus.bindMsg("^sra5 Text=ellipse",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {

                        switch(e){
                            case CREER :
                                e = Etats.C_FORME;
                                f = Forme.ELLIPSE;
                                tc = new TimerTask() {

                                    @Override
                                    public void run() {
                                        e = Etats.INIT;
                                        System.out.println(e);
                                        try {
                                            creerForme(f,xc,yc,c);
                                        } catch (IvyException ex) {
                                            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        xc = 0;
                                        yc = 0;
                                        c = "white";
                                        f = Forme.NULL;
                                    }
                                };
                                t = new Timer();
                                t.schedule(tc,3000);

                                System.out.println(e+" "+f);
                                break;
                                case DEPLACER :
                                   e =Etats.D_FORME;
                                   f = Forme.ELLIPSE;
                                   break;
                        }
                    
                }
            });
            bus.bindMsg("^sra5 Text=ici",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {

                        switch(e){
                            case C_FORME :
                                t.cancel();
                                e = Etats.C_POSITION;
                                System.out.println(e);
                                break;
                            case C_ATTENTE_POSITION :
                                t2.cancel();
                                t = new Timer();
                                tc = new TimerTask() {

                                    @Override
                                    public void run() {
                                        e = Etats.INIT;
                                        System.out.println(e);
                                        try {
                                            creerForme(f,xc,yc,c);
                                        } catch (IvyException ex) {
                                            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        xc = 0;
                                        yc = 0;
                                        c = "white";
                                        f = Forme.NULL;
                                    }
                                };
                                t.schedule(tc,3000);
                                break;
                            case D_FORME_SELECTIONNEE :
                                e = Etats.D_POSITION;
                                System.out.println(e);
                               
                        }

                }
            });
            bus.bindMsg("^sra5 Text=rouge",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {

                        switch(e){
                            case C_FORME :
                                t.cancel();
                                c = "red";
                                System.out.println(c);
                                tc = new TimerTask() {

                                    @Override
                                    public void run() {
                                        e = Etats.INIT;
                                        System.out.println(e);
                                        try {
                                            creerForme(f,xc,yc,c);
                                        } catch (IvyException ex) {
                                            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        xc = 0;
                                        yc = 0;
                                        c = "white";
                                        f = Forme.NULL;
                                    }
                                };
                                t = new Timer();
                                t.schedule(tc,3000);

                                break;
                        }
                }
            });
            bus.bindMsg("^sra5 Text=vert",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {

                        switch(e){
                            case C_FORME :
                                t.cancel();
                                c = "green";
                                System.out.println(c);
                                tc = new TimerTask() {

                                    @Override
                                    public void run() {
                                        e = Etats.INIT;
                                        System.out.println(e);
                                        try {
                                            creerForme(f,xc,yc,c);
                                        } catch (IvyException ex) {
                                            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        xc = 0;
                                        yc = 0;
                                        c = "white";
                                        f = Forme.NULL;
                                    }
                                };
                                t = new Timer();
                                t.schedule(tc,3000);
                                
                                break;
                        }
                }
            });
            bus.bindMsg("^sra5 Text=bleu",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {

                        switch(e){
                            case C_FORME :
                               t.cancel();
                               c = "blue";
                               System.out.println(c);
                               tc = new TimerTask() {

                                    @Override
                                    public void run() {
                                        e = Etats.INIT;
                                        System.out.println(e);
                                        try {
                                            creerForme(f,xc,yc,c);
                                        } catch (IvyException ex) {
                                            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        xc = 0;
                                        yc = 0;
                                        c = "white";
                                        f = Forme.NULL;
                                    }
                                };
                               t = new Timer();
                               t.schedule(tc,3000);
                                break;
                        }
                }
            });

            bus.bindMsg("^Palette:MouseReleased x=([0-9]*) y=([0-9]*)",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    
                        switch(e){
                            case C_POSITION :
                                t.cancel();
                                xc = Integer.parseInt(args[0]);
                                yc = Integer.parseInt(args[1]);
                                e =  Etats.C_FORME;
                                tc = new TimerTask() {

                                    @Override
                                    public void run() {
                                        e = Etats.INIT;
                                        System.out.println(e);
                                        try {
                                            creerForme(f,xc,yc,c);
                                        } catch (IvyException ex) {
                                            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        xc = 0;
                                        yc = 0;
                                        c = "white";
                                        f = Forme.NULL;
                                    }
                                };
                                t = new Timer();
                                t.schedule(tc,3000);

                                break;
                            case C_FORME :
                                e = Etats.C_ATTENTE_POSITION;
                                t2 = new Timer();
                                t2.schedule(new TimerTask() {
                                     @Override
                                     public void run() {
                                         e = Etats.C_FORME;
                                         xc = 0;
                                         yc = 0;
                                      }
                                 }, 3000);
                                xc = Integer.parseInt(args[0]);
                                yc = Integer.parseInt(args[1]);
                                break;
                            case D_FORME :
                                x0 = Integer.parseInt(args[0]);
                                y0 = Integer.parseInt(args[1]);
                                x1 = 0;
                                y1 = 0;
                                try {
                                    bus.sendMsg("Palette:TesterPoint x="+args[0]+" y="+args[1]);
                                } catch (IvyException ex) {
                                    Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case D_POSITION :
                                e = Etats.D_FORME_SELECTIONNEE;
                                x1 = Integer.parseInt(args[0]);
                                y1 = Integer.parseInt(args[1]);
                                //Test pour le moment, Timers implémentés plus tard
                                try {
                                    deplacerForme(x1-x0,y1-y0,idforme);
                                    e = Etats.INIT;
                                } catch (IvyException ex) {
                                    Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                        }
                        
                        //TODO Jeremy mettre dans un état
                       /* try {
                            //bus.sendMsg("J'ai :"+args[0] + " : "+ args[1]);
                            if(!waiting_loc) {
                                bus.sendMsg("Palette:TesterPoint x="+args[0]+" y="+args[1]);
                            }
                            else {
                                int tmpx = Integer.parseInt(args[0]) - (Integer.parseInt(x) + 40);
                                int tmpy = Integer.parseInt(args[1]) - (Integer.parseInt(y) + 20);
                                bus.sendMsg("Palette:DeplacerObjet nom="+lastSelected+" x="+tmpx+" y="+tmpy);
                                waiting_loc = false;
                            }
                            x = ""+(Integer.parseInt(args[0])-40);
                            y = ""+(Integer.parseInt(args[1])-20);
                        } catch (IvyException ex) {
                            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                        }   */
                    
                }

                
            });
            
            
            bus.bindMsg("^Palette:ResultatTesterPoint x=([0-9]*) y=([0-9]*) nom=(.*)",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    //TODO Jeremy a mettre dans un état
                    //lastSelected = args[2];    
                    switch(e){
                        case D_FORME :
                            e = Etats.D_FORME_SELECTIONNEE;
                            idforme = args[2];
                            System.out.println(idforme);
                            break;
                    }
                }
            });
            /*
            //"Palette:Info nom=E2 x=79 y=68 longueur=100 hauteur=50 couleurFond=white couleurContour=black"
            bus.bindMsg("^Palette:Info x=([A-Z]*[0-9]*) x=([0-9]*) y=([0-9]*)",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                        //bus.sendMsg("J'ai :"+args[0] + " : "+ args[1]);
                        x_last = ""+(Integer.parseInt(args[1])+40);
                        y_last = ""+(Integer.parseInt(args[2])+20);
                        System.out.println("Got x="+x+" y="+y);
                }
            });
            */
            
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
            case "Deplacer" :
                waiting_loc = true;
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
    
    private void creerForme(Forme f, int x, int y, String c) throws IvyException {
        switch(f){
            case RECTANGLE :
                bus.sendMsg("Palette:CreerRectangle x="+xc+" y="+yc+" longueur=80 hauteur=40 couleurFond="+c);
                break;
            case ELLIPSE :
                bus.sendMsg("Palette:CreerEllipse x="+xc+" y="+yc+" longueur=80 hauteur=40 couleurFond="+c);
                break;
        }
    }
    
    private void deplacerForme(int x1, int y1, String idforme) throws IvyException {
           bus.sendMsg("Palette:DeplacerObjet nom="+idforme+" x="+x1+" y="+y1);
    }
}
