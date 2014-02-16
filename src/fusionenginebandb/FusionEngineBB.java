/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fusionenginebandb;

import fr.dgac.ivy.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;

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
                @Override
                public void receive(IvyClient client, String[] args) {
                    parsecmdICAR(args[0]);
                }
            });
           
             bus.bindMsg("^sra5 Text=(.*) Confidence=(.*)",new IvyMessageListener() {
                 @Override
                public void receive(IvyClient client, String[] args) {
                       parsecmdSRA(args[0]);
                    }
            });

            bus.bindMsg("^Palette:MouseReleased x=([0-9]*) y=([0-9]*)",new IvyMessageListener() {
                @Override
                public void receive(IvyClient client, String[] args) {
                    
                        switch(e){
                            case C_POSITION :
                                t.cancel();
                                xc = Integer.parseInt(args[0]);
                                yc = Integer.parseInt(args[1]);
                                e =  Etats.C_FORME;
                                timerCreer();
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
                            case C_COULEUR: 
                                t = new Timer();
                                t.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        c ="white";
                                        e = Etats.C_FORME;
                                        System.out.println(e);
                                        timerCreer();
                                    }
                                }, 2000);
                                try {
                                    bus.sendMsg("Palette:TesterPoint x="+args[0]+" y="+args[1]);
                                } catch (IvyException ex) {
                                    Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case D_FORME :
                                x0 = Integer.parseInt(args[0]);
                                y0 = Integer.parseInt(args[1]);
                                x1 = x0;
                                y1 = y0;
                                try {
                                    bus.sendMsg("Palette:TesterPoint x="+args[0]+" y="+args[1]);
                                } catch (IvyException ex) {
                                    Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case D_COULEUR :
                                x0 = Integer.parseInt(args[0]);
                                y0 = Integer.parseInt(args[1]);
                                x1 = x0;
                                y1 = y0;
                                try {
                                    System.out.println("testPt");
                                    bus.sendMsg("Palette:TesterPoint x="+args[0]+" y="+args[1]);
                                } catch (IvyException ex) {
                                    Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case D_FORME_SELECTIONNEE :
                                t.cancel();
                                e = Etats.D_ATTENTE_POSITION;
                                System.out.println(e);
                                x1 = Integer.parseInt(args[0]);
                                y1 = Integer.parseInt(args[1]);
                                t2 = new Timer();
                                t2.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        x1 = x0;
                                        y1 = y0;
                                        e = Etats.D_FORME_SELECTIONNEE;
                                        System.out.println(e);
                                        timerDeplacer();
                                    }
                                }, 2000);
                                break;
                            case D_POSITION :
                                    x1 = Integer.parseInt(args[0]);
                                    y1 = Integer.parseInt(args[1]);
                                    timerDeplacer();
                                    break;
                            case INIT:
                                //Interdit
                                break;
                            case CREER :
                                //Interdit
                                break;
                            case C_ATTENTE_POSITION:
                                //Interdit
                                break;
                            case DEPLACER:
                                //Interdit
                                break;
                            case D_ATTENTE_POSITION:
                                //Interdit
                                break;
                            case D_CAMERA:
                                //Interdit
                                break;
                                
                        }
                }
            });
            
            bus.bindMsg("^Palette:ResultatTesterPoint x=([0-9]*) y=([0-9]*) nom=(.*)",new IvyMessageListener() {
                @Override
                public void receive(IvyClient client, String[] args) {
                    switch(e){
                        case C_COULEUR :
                            t.cancel();
                            idforme = args[2];
                            try {
                                bus.sendMsg("Palette:DemanderInfo nom="+idforme);
                            } catch (IvyException ex) {
                                Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        
                        case D_FORME :
                            if(args[2].startsWith("E") && f.equals(Forme.ELLIPSE) ){
                                
                                e = Etats.D_FORME_SELECTIONNEE;
                                idforme = args[2];
                                System.out.println(e + " " + idforme);
                                timerDeplacer();
                            }
                            
                            if(args[2].startsWith("R") && f.equals(Forme.RECTANGLE)){
                                
                                e = Etats.D_FORME_SELECTIONNEE;
                                idforme = args[2];
                                System.out.println(e + " " + idforme);
                                timerDeplacer();
                            }   
                            break;
                            
                        case D_COULEUR: 
                            if(args[2].startsWith("E") && f.equals(Forme.ELLIPSE) ){
                                
                                idforme = args[2];
                                try {
                                    bus.sendMsg("Palette:DemanderInfo nom="+idforme);
                                    System.out.println("demandeInfo");
                                } catch (IvyException ex) {
                                    Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            
                            if(args[2].startsWith("R") && f.equals(Forme.RECTANGLE)){
                                
                                idforme = args[2];
                                try {
                                    
                                    bus.sendMsg("Palette:DemanderInfo nom="+idforme);
                                    System.out.println("demandeInfo");
                                } catch (IvyException ex) {
                                    Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }   
                            break;
                            
                        case INIT:
                            //Interdit
                            break;
                        case CREER:
                            //Interdit
                            break;
                        case C_FORME:
                            //Interdit
                            break;
                        case C_POSITION:
                            //Interdit
                            break;

                        case C_ATTENTE_POSITION:
                            //Interdit
                            break;
                        case DEPLACER:
                            //Interdit
                            break;

                        case D_FORME_SELECTIONNEE:
                            //Interdit
                            break;
                        case D_POSITION:
                            //Interdit
                            break;

                        case D_ATTENTE_POSITION:
                            //Interdit
                            break;
                        case D_CAMERA:
                        //Interdit
                        break;
                    }
                }
            });
            
             bus.bindMsg("^Palette:Info nom=([A-Z]*[0-9]*) x=([0-9]*) y=([0-9]*) longueur=([0-9]*) hauteur=([0-9]*) couleurFond=(.*) couleurContour=(.*)",new IvyMessageListener() {
                public void receive(IvyClient client, String[] args) {
                    switch(e){
                        case C_COULEUR:
                            c = args[5];
                            e = Etats.C_FORME;
                            System.out.println(e);
                            timerCreer();
                            break;
                        case D_COULEUR :
                            System.out.println("Couleur fond : "+args[5]);
                            if(c.equals(args[5])){  
                                t.cancel();
                                e = Etats.D_FORME_SELECTIONNEE;
                                System.out.println(e);
                                timerDeplacer();
                            }   
                            break;
                        case INIT:
                            //Interdit
                            break;
                        case CREER:
                            //Interdit
                            break;
                        case C_FORME:
                            //Interdit
                            break;
                        case C_POSITION:
                            //Interdit
                            break;
                        case C_ATTENTE_POSITION:
                            //Interdit
                            break;
                        case DEPLACER:
                            //Interdit
                            break;
                        case D_FORME:
                            //Interdit
                            break;
                        case D_FORME_SELECTIONNEE:
                            //Interdit
                            break;
                        case D_POSITION:
                            //Interdit
                            break;
                        case D_ATTENTE_POSITION:
                            //Interdit
                            break;
                        case D_CAMERA:
                            //Interdit
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
            
            TangibleTracker track = new TangibleTracker();
            
            track.addPropertyChangeListener("Color", new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if(evt.getNewValue() == "ROUGE") {
                        switch(e){
                            case C_COULEUR :
                                t.cancel();
                                c = "red";
                                System.out.println(c);
                                timerCreer();
                                break;
                                case INIT:
                                    //Interdit
                                    break;
                                case CREER:
                                    //Interdit
                                    break; 
                                case C_FORME:
                                    //Interdit
                                    break; 
                                case C_POSITION:
                                    //Interdit
                                    break; 
                                case C_ATTENTE_POSITION:
                                    //Interdit
                                    break; 
                                case DEPLACER:
                                    //Interdit
                                    break; 
                                case D_FORME:
                                    //Interdit
                                    break; 
                                case D_FORME_SELECTIONNEE :
                                    //Interdit
                                    break; 
                                case D_POSITION:
                                    //Interdit
                                    break; 
                                case D_COULEUR:
                                    //Interdit
                                    break; 
                                case D_ATTENTE_POSITION:
                                    //Interdit
                                    break; 
                                case D_CAMERA:
                                    //Interdit
                                    break; 
                        }
                    }
                    else if(evt.getNewValue() == "BLEU"){
                        switch(e){
                            case C_COULEUR :
                                t.cancel();
                                c = "blue";
                                System.out.println(c);
                                timerCreer();
                                break;
                                case INIT:
                                    //Interdit
                                    break; 
                                case CREER:
                                    //Interdit
                                    break; 
                                case C_FORME:
                                    //Interdit
                                    break; 
                                case C_POSITION:
                                    //Interdit
                                    break; 
                                case C_ATTENTE_POSITION:
                                    //Interdit
                                    break; 
                                case DEPLACER:
                                    //Interdit
                                    break; 
                                case D_FORME:
                                    //Interdit
                                    break; 
                                case D_FORME_SELECTIONNEE :
                                    //Interdit
                                    break; 
                                case D_POSITION:
                                    //Interdit
                                    break; 
                                case D_COULEUR:
                                    //Interdit
                                    break; 
                                case D_ATTENTE_POSITION:
                                    //Interdit
                                    break; 
                                case D_CAMERA:
                                    //Interdit
                                    break; 
                        }
                    }
                    else {
                        switch(e){
                            case C_COULEUR :
                                t.cancel();
                                c = "green";
                                System.out.println(c);
                                timerCreer();
                                break;
                                case INIT:
                                    //Interdit
                                    break; 
                                case CREER:
                                    //Interdit
                                    break; 
                                case C_FORME:
                                    //Interdit
                                    break; 
                                case C_POSITION:
                                    //Interdit
                                    break; 
                                case C_ATTENTE_POSITION:
                                    //Interdit
                                    break; 
                                case DEPLACER:
                                    //Interdit
                                    break; 
                                case D_FORME:
                                    //Interdit
                                    break; 
                                case D_FORME_SELECTIONNEE :
                                    //Interdit
                                    break; 
                                case D_POSITION:
                                    //Interdit
                                    break; 
                                case D_COULEUR:
                                    //Interdit
                                    break; 
                                case D_ATTENTE_POSITION:
                                    //Interdit
                                    break; 
                                case D_CAMERA:
                                    //Interdit
                                    break; 
                        }
                    }
                }
            });
            
            track.addPropertyChangeListener("LocationChange", new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    switch(e){
                        case D_CAMERA :
                            t.cancel();
                            Vector3f v = (Vector3f) evt.getNewValue();
                            try {
                                System.out.println(e + " -> bouger " + idforme);
                                System.out.println("x = " + v.x + " | y = " + v.y);
                                bus.sendMsg("Palette:DeplacerObjet nom="+idforme+" x="+(int)(v.x*30)+" y="+(int)(-v.y*30));
                                timerCamera();
                            } catch (IvyException ex) {
                                Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        case INIT:
                            //Interdit
                        break; 
                        case CREER:
                            //Interdit
                        break; 
                        case C_FORME:
                            //Interdit
                        break; 
                        case C_POSITION:
                            //Interdit
                        break; 
                        case C_COULEUR:
                            //Interdit
                        break; 
                        case C_ATTENTE_POSITION:
                            //Interdit
                        break; 
                        case DEPLACER:
                            //Interdit
                        break; 
                        case D_FORME:
                            //Interdit
                        break; 
                        case D_FORME_SELECTIONNEE :
                            //Interdit
                        break; 
                        case D_POSITION:
                            //Interdit
                        break; 
                        case D_COULEUR:
                            //Interdit
                        break; 
                        case D_ATTENTE_POSITION:
                            //Interdit
                        break; 
                    }
                }


                
            });
            
            bus.start(null);
        } catch (Exception ex) {
            Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void parsecmdICAR(String string) {
        switch(string) {
            case "Rectangle" :
                switch(e){
                    case CREER :
                        e = Etats.C_FORME;
                        f = Forme.RECTANGLE;
                        System.out.println(e+" "+f);
                        timerCreer();
                        break;
                    case DEPLACER :
                        e =Etats.D_FORME;
                        f = Forme.RECTANGLE;
                        break;
                    case INIT:
                        //Interdit
                        break;
                    case C_FORME:
                        //Interdit
                        break;
                    case C_POSITION:
                        //Interdit
                        break;
                    case C_COULEUR:
                        //Interdit
                        break;
                    case C_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case D_FORME:
                        //Interdit
                        break;
                    case D_FORME_SELECTIONNEE:
                        //Interdit
                        break;
                    case D_POSITION:
                        //Interdit
                        break;
                    case D_COULEUR:
                        //Interdit
                        break;
                    case D_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case D_CAMERA:
                        //Interdit
                        break;
                }
                //rep = "CreerRectangle x="+x+" y="+y+" longueur=80 hauteur=40 couleurFond=white";
                break;
            case "Ellipse" :
                switch(e){
                    case CREER :
                        e = Etats.C_FORME;
                        f = Forme.ELLIPSE;
                        timerCreer();
                        System.out.println(e+" "+f);
                        break;
                    case DEPLACER :
                        e =Etats.D_FORME;
                        f = Forme.ELLIPSE;
                        break;
                    case INIT:
                        //Interdit
                        break;
                    case C_FORME:
                        //Interdit
                        break;
                    case C_POSITION:
                        //Interdit
                        break;
                    case C_COULEUR:
                        //Interdit
                        break;
                    case C_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case D_FORME:
                        //Interdit
                        break;
                    case D_FORME_SELECTIONNEE:
                        //Interdit
                        break;
                    case D_POSITION:
                        //Interdit
                        break;
                    case D_COULEUR:
                        //Interdit
                        break;
                    case D_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case D_CAMERA:
                        //Interdit
                        break;  
                }
                //rep = "CreerEllipse x="+x+" y="+y;
                break;
            /*
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
            */
            case "Deplacer" :
                switch (e) {
                    case INIT :
                        e = Etats.DEPLACER;
                        System.out.println(e);
                        break;
                    case CREER:
                        //Interdit
                        break;
                    case C_FORME:
                        //Interdit
                        break;
                    case C_POSITION:
                        //Interdit
                        break;
                    case C_COULEUR:
                        //Interdit
                        break;
                    case C_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case DEPLACER:
                        //Interdit
                        break;
                    case D_FORME:
                        //Interdit
                        break;
                    case D_FORME_SELECTIONNEE:
                        //Interdit
                        break;
                    case D_POSITION:
                        //Interdit
                        break;
                    case D_COULEUR:
                        //Interdit
                        break;
                    case D_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case D_CAMERA:
                        //Interdit
                        break;
                } 
                //waiting_loc = true;
                break;
                
                case "Annuler" :
                    e = Etats.INIT;
                    System.out.println(e);
                    xc = 0;
                    yc = 0;
                    x0 = 0;
                    y0 = 0;
                    x1 = 0;
                    y1 = 0;
                    idforme = "";
                    c = "white";
                    f = Forme.NULL;
                    if(t != null){
                        t.purge();
                    }
                    if(t2 != null){
                        t2.purge();
                    }
                
                //waiting_loc = true;
                break;
        }
    }
    

    private void parsecmdSRA(String string) {
        String cmd = skip3suspension(string);
        //System.out.println("Recu : " + cmd);
        switch(cmd) {
            case "annuler":
                e = Etats.INIT;
                System.out.println(e);
                xc = 0;
                yc = 0;
                x0 = 0;
                y0 = 0;
                x1 = 0;
                y1 = 0;
                idforme = "";
                c = "white";
                f = Forme.NULL;
                if(t != null){
                    t.purge();
                }
                if(t2 != null){
                    t2.purge();
                }
                break;
                
            case "creer" :
                //System.out.println("Creation");
                switch (e) {
                     case INIT :
                         e = Etats.CREER;
                         System.out.println(e);
                         break;
                    case CREER:
                        //Interdit
                        break;
                    case C_FORME:
                        //Interdit
                        break;
                    case C_POSITION:
                        //Interdit
                        break;
                    case C_COULEUR:
                        //Interdit
                        break;
                    case C_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case DEPLACER:
                        //Interdit
                        break;
                    case D_FORME:
                        //Interdit
                        break;
                    case D_FORME_SELECTIONNEE:
                        //Interdit
                        break;
                    case D_POSITION:
                        //Interdit
                        break;
                    case D_COULEUR:
                        //Interdit
                        break;
                    case D_ATTENTE_POSITION:
                        //Interdit
                        break;
                        case D_CAMERA:
                                //Interdit
                                break;
                }  
                break;
                
            case "deplacer ce":
                switch (e) {
                    case INIT :
                        e = Etats.DEPLACER;
                        System.out.println(e);
                        break;
                    case CREER:
                        //Interdit
                        break;
                    case C_FORME:
                        //Interdit
                        break;
                    case C_POSITION:
                        //Interdit
                        break;
                    case C_COULEUR:
                        //Interdit
                        break;
                    case C_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case DEPLACER:
                        //Interdit
                        break;
                    case D_FORME:
                        //Interdit
                        break;
                    case D_FORME_SELECTIONNEE:
                        //Interdit
                        break;
                    case D_POSITION:
                        //Interdit
                        break;
                    case D_COULEUR:
                        //Interdit
                        break;
                    case D_ATTENTE_POSITION:
                        //Interdit
                        break;
                        case D_CAMERA:
                                //Interdit
                                break;
                } 
                break;
                
            case "comme cela":
                switch (e) {
                    case D_FORME_SELECTIONNEE :
                        e = Etats.D_CAMERA;
                        System.out.println(e);
                        break;
                } 
                break;
                
            case "rectangle" :
                switch(e){
                    case CREER :
                        e = Etats.C_FORME;
                        f = Forme.RECTANGLE;
                        System.out.println(e+" "+f);
                        timerCreer();
                        break;
                    case DEPLACER :
                        e =Etats.D_FORME;
                        f= Forme.RECTANGLE;
                        System.out.println(e);
                        break;
                    case INIT:
                        //Interdit
                        break;
                    case C_FORME:
                        //Interdit
                        break;
                    case C_POSITION:
                        //Interdit
                        break;
                    case C_COULEUR:
                        //Interdit
                        break;
                    case C_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case D_FORME:
                        //Interdit
                        break;
                    case D_FORME_SELECTIONNEE:
                        //Interdit
                        break;
                    case D_POSITION:
                        //Interdit
                        break;
                    case D_COULEUR:
                        //Interdit
                        break;
                    case D_ATTENTE_POSITION:
                        //Interdit
                    break;
                        case D_CAMERA:
                                //Interdit
                                break;
                }
                //rep = "CreerRectangle x="+x+" y="+y+" longueur=80 hauteur=40 couleurFond=white";
                break;
                
            case "ellipse" :
                switch(e){
                    case CREER :
                        e = Etats.C_FORME;
                        f = Forme.ELLIPSE;
                        timerCreer();
                        System.out.println(e+" "+f);
                        break;
                    case DEPLACER :
                        e =Etats.D_FORME;
                        f = Forme.ELLIPSE;
                        break;
                    case INIT:
                        //Interdit
                        break;
                    case C_FORME:
                        //Interdit
                        break;
                    case C_POSITION:
                        //Interdit
                        break;
                    case C_COULEUR:
                        //Interdit
                        break;
                    case C_ATTENTE_POSITION:
                        //Interdit
                        break;
                    case D_FORME:
                        //Interdit
                        break;
                    case D_FORME_SELECTIONNEE:
                        //Interdit
                        break;
                    case D_POSITION:
                        //Interdit
                        break;
                    case D_COULEUR:
                        //Interdit
                        break;
                    case D_ATTENTE_POSITION:
                        //Interdit
                         break;
                        case D_CAMERA:
                                //Interdit
                                break;
                }
                //rep = "CreerEllipse x="+x+" y="+y;
                break;
                
            case "ici" :
                switch(e){
                    case C_FORME :
                        t.cancel();
                        e = Etats.C_POSITION;
                        System.out.println(e);
                        break;
                    case C_ATTENTE_POSITION :
                        t2.cancel();
                        timerCreer();
                        break;
                    case D_FORME_SELECTIONNEE :
                        e = Etats.D_POSITION;
                        System.out.println(e);
                        t.cancel();
                        break;
                    case D_ATTENTE_POSITION :
                        t2.cancel();
                        e = Etats.D_FORME_SELECTIONNEE;
                        System.out.println(e);
                        timerDeplacer();
                        break;
                    case INIT:
                        //Interdit
                        break;
                    case CREER:
                        //Interdit
                        break;
                    case C_POSITION:
                        //Interdit
                        break;
                    case C_COULEUR:
                        //Interdit
                        break;
                    case DEPLACER:
                        //Interdit
                        break;
                    case D_FORME:
                        //Interdit
                        break;
                    case D_POSITION:
                        //Interdit
                        break;
                    case D_COULEUR:
                        //Interdit
                        break;
                        case D_CAMERA:
                                //Interdit
                                break;
                    
                }
                break;
                
            case "rouge":
                switch(e){
                    case C_FORME :
                        t.cancel();
                        c = "red";
                        System.out.println(c);
                        timerCreer();
                        break;
                    case D_FORME :
                        e = Etats.D_COULEUR;
                        System.out.println(e);
                        c = "red";
                        timerDeplacerAvecCouleur();
                        break;
                        case INIT:
                            //Interdit
                            break;
                        case CREER:
                            //Interdit
                            break;
                        case C_POSITION:
                            //Interdit
                            break;
                        case C_COULEUR:
                            //Interdit
                            break;
                        case C_ATTENTE_POSITION:
                            //Interdit
                            break;
                        case DEPLACER:
                            //Interdit
                            break;
                        case D_FORME_SELECTIONNEE:
                            //Interdit
                            break;
                        case D_POSITION:
                            //Interdit
                            break;
                        case D_COULEUR:
                            //Interdit
                            break;
                        case D_ATTENTE_POSITION:
                            //Interdit
                        break;
                            case D_CAMERA:
                                //Interdit
                                break;
                }
                break;
                
            case "vert":
                switch(e){
                    case C_FORME :
                        t.cancel();
                        c = "green";
                        System.out.println(c);
                        timerCreer();
                        break;
                    case D_FORME :
                        e = Etats.D_COULEUR;
                        System.out.println(e);
                        c = "green";
                        timerDeplacerAvecCouleur();
                        break;
                        case INIT:
                            //Interdit
                            break;
                        case CREER:
                            //Interdit
                            break;
                        case C_POSITION:
                            //Interdit
                            break;
                        case C_COULEUR:
                            //Interdit
                            break;
                        case C_ATTENTE_POSITION:
                            //Interdit
                            break;
                        case DEPLACER:
                            //Interdit
                            break;
                        case D_FORME_SELECTIONNEE:
                            //Interdit
                            break;
                        case D_POSITION:
                            //Interdit
                            break;
                        case D_COULEUR:
                            //Interdit
                            break;
                        case D_ATTENTE_POSITION:
                            //Interdit
                            break;
                            case D_CAMERA:
                                //Interdit
                                break;
                }
                break;
                
            case "bleu":
                switch(e){
                    case C_FORME :
                        t.cancel();
                        c = "blue";
                        System.out.println(c);
                        timerCreer();
                        break;
                    case D_FORME :
                        e = Etats.D_COULEUR;
                        System.out.println(e);
                        c = "blue";
                        timerDeplacerAvecCouleur();
                        break;
                        case INIT:
                            //Interdit
                            break;
                        case CREER:
                            //Interdit
                            break;
                        case C_POSITION:
                            //Interdit
                            break;
                        case C_COULEUR:
                            //Interdit
                            break;
                        case C_ATTENTE_POSITION:
                            //Interdit
                            break;
                        case DEPLACER:
                            //Interdit
                            break;
                        case D_FORME_SELECTIONNEE:
                            //Interdit
                            break;
                        case D_POSITION:
                            //Interdit
                            break;
                        case D_COULEUR:
                            //Interdit
                            break;
                        case D_ATTENTE_POSITION:
                            //Interdit
                            break;
                            case D_CAMERA:
                                //Interdit
                                break;
                }
                break;
                
            case "de cette couleur":
                switch(e){
                    case C_FORME:
                        t.cancel();
                        e = Etats.C_COULEUR;
                        System.out.println(e);
                        break;
                        case INIT:
                            //Interdit
                            break;
                        case CREER:
                            //Interdit
                            break;
                        case C_POSITION:
                            //Interdit
                            break;
                        case C_COULEUR:
                            //Interdit
                            break;
                        case C_ATTENTE_POSITION:
                            //Interdit
                            break;
                        case DEPLACER:
                            //Interdit
                            break;
                        case D_FORME:
                            //Interdit
                            break;
                        case D_FORME_SELECTIONNEE:
                            //Interdit
                            break;
                        case D_POSITION:
                            //Interdit
                            break;
                        case D_COULEUR:
                            //Interdit
                            break;
                        case D_ATTENTE_POSITION:
                            //Interdit
                            break;
                            case D_CAMERA:
                                //Interdit
                                break;
                }
                break;
            /*    
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
                break;*/
        }
    }

    private String skip3suspension(String string) {
        String ret = string.replace("...", "");
        if(ret.charAt(0) == ' ') {
            ret = ret.replaceFirst(" ", "");
        }
        return ret;
    }
    
    private void creerForme(Forme f, int x, int y, String c) throws IvyException {
        switch(f){
            case RECTANGLE :
                bus.sendMsg("Palette:CreerRectangle x="+(xc)+" y="+(yc)+" longueur=80 hauteur=40 couleurFond="+c);
                break;
            case ELLIPSE :
                bus.sendMsg("Palette:CreerEllipse x="+(xc)+" y="+(yc)+" longueur=80 hauteur=40 couleurFond="+c);
                break;
        }
    }
    
    private void deplacerForme(int x1, int y1, String idforme) throws IvyException {
           bus.sendMsg("Palette:DeplacerObjet nom="+idforme+" x="+x1+" y="+y1);
    }
    
    private void timerCreer(){
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
    }
    
    private void timerDeplacer(){
        t = new Timer();
        tc = new TimerTask() {

            @Override
            public void run() {
                e = Etats.INIT;
                System.out.println(e);
                try {
                    deplacerForme(x1-x0, y1-y0, idforme);
                } catch (IvyException ex) {
                    Logger.getLogger(FusionEngineBB.class.getName()).log(Level.SEVERE, null, ex);
                }
                x0=0;
                y0=0;
                x1=0;
                y1=0;
                c = "white";
                f = Forme.NULL;
                idforme = "";
            }
        };
        t.schedule(tc, 10000);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        FusionEngineBB fusionEngineBB = new FusionEngineBB();
    }
    
    private void timerDeplacerAvecCouleur(){
    
        t = new Timer();
        tc = new TimerTask() {
            @Override
            public void run() {
                e = Etats.INIT;
                System.out.println(e);
                x0=0;
                y0=0;
                x1=0;
                y1=0;
                c = "white";
                f = Forme.NULL;
                idforme = "";
            }
        };
        t.schedule(tc,3000);
    }
    
    private void timerCamera() {
        t = new Timer();
        tc = new TimerTask() {
            @Override
            public void run() {
                e = Etats.INIT;
                System.out.println(e);
                x0=0;
                y0=0;
                x1=0;
                y1=0;
                c = "white";
                f = Forme.NULL;
                idforme = "";
            }
        };
        t.schedule(tc,3000);
    }
}
