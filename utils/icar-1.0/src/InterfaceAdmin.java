import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.Set;
import java.util.Calendar;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.FilenameFilter;
import java.io.File;

import icar.IcarPreview;
import icar.util.TimePrecision;
import icar.event.*;

public class InterfaceAdmin extends Interface 
    implements ItemListener, IcarRapportClasseListener, IcarRapportTraceListener
							 
{

    // ATTRIBUTS

    // La fenetre de log
    private TextArea fenetreLog;

    // Le conteneur de la liste des classes
    private List listeClasse;

    // Le conteneur de la liste de trace
    private ComponentScrollList listeTraces;

    // Indicateur si l'echantillonage est modifiable
    private boolean echantillonageCanBeModified;


    // CONSTRUCTEUR
    public InterfaceAdmin() {
	super();

	// Reflete le changement de texte de la zone de saisie
	// au composant IcarComponent
	champTexte.addKeyListener( new KeyListener() {

		public void keyPressed(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {
		    zoneIcar.setMotif(champTexte.getText());
		} 
		public void keyTyped(KeyEvent e) {} 
	    }
				      );

	// Cas à cocher Apprentissage
	Checkbox caseApprentissage = new Checkbox("Apprentissage");
	paneBas.add(caseApprentissage, BorderLayout.WEST);
	// L'interface recevra les évenements de cette case
	caseApprentissage.addItemListener(this);

	//-- Creation de la barre de menu
	this.creerBarreMenu();

	//-- Creation de la liste des classe
	this.creerListeClasse();

	//-- Liste des traces de la classe
	this.creerListeTrace();

	//-- Creation de la fenetre de log (unwritable)
	fenetreLog = 
	    new TextArea("",
			 6,10, 
			 TextArea.SCROLLBARS_VERTICAL_ONLY );
	fenetreLog.setEditable(false);
	// Message de début
	ecrireFenetreLog(nomLogiciel + " build " + versionLogiciel);

	this.add(fenetreLog, BorderLayout.SOUTH);


	// S'ajoute en listener pour recevoir la liste des classes
	// et des traces lorsqu'ells chantent
	zoneIcar.addIcarRapportClasseListener(this);
	zoneIcar.addIcarRapportTraceListener(this);

	// Retaille la fenetre au mieux
	pack();

	// Centre la frame sur l'ecran
	this.centrer();
      
	// Affiche la fenetre
	show();
    }

    // METHODES PRIVEES

    // Creer le composant liste des exemples d'une classe
    private void creerListeTrace() {

	// Creation du composant
	listeTraces = new ComponentScrollList(120,3);

	// Ajout a la frame
	this.add(listeTraces, BorderLayout.EAST);

	// Evenement: objet selectionne dans la liste
	listeTraces.addItemListener(this);

	// Creation du menu popup associé
	final PopupMenu listeClassePopup = creerPopupMenu(new ActionListener() {
		// Action a affectuer lors de la suppresion d'une classe
		public void actionPerformed(ActionEvent actionevent) {		    
		    // Log
		    ecrireFenetreLog("Suppresion d'un exemple de la classe " 
				     + listeClasse.getSelectedItem());		    
		    // Demande de suppression

		    // Le parametre
		    //Vector temp = new Vector();
		    // Le motif en 0
		    //temp.add(listeClasse.getSelectedItem());
		    // Le trace en 1
		    //temp.add(((ZoneApercu)listeTraces.getSelectedItem()).getTrace());
		    // Envoie du message
		    //alertDictionnaryActionListeners(
                    //            DictionnaryActionEvent.DELETE_CLASS_EXEMPLE,
		    //	temp);
		    zoneIcar.deleteClassExemple(listeClasse.getSelectedItem(),
						((IcarPreview)listeTraces.getSelectedItem()).getTrace());
		    
		}
	    }

        );

	// Ouverture du menu
	listeTraces.addMouseListener( new MouseListener () {
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent mouseevent) {
		    // Popup = Clic du bouton droit
		    if((mouseevent.getButton() == MouseEvent.BUTTON3)
		       && (listeTraces.getSelectedItem()!= null)) {	
			// Affiche le menu
			listeClassePopup.show(mouseevent.getComponent(), 
					      mouseevent.getX(), 
					      mouseevent.getY());
		    }
		    
		}
	    }
        );

	listeTraces.add(listeClassePopup);


    }

    // Creer le composant liste des classes
    private void creerListeClasse() {
	// Creation du composant
	this.listeClasse = new List();

	// Ajout a la frame
	this.add(listeClasse, BorderLayout.CENTER);

	// Evenement: objet selectionne dans la liste
	listeClasse.addItemListener(this);

	// Creation du menu popup associé
	final PopupMenu listeClassePopup = creerPopupMenu(new ActionListener() {
		// Action a affectuer lors de la suppresion d'une classe
		public void actionPerformed(ActionEvent actionevent) {		    
		    // Log
		    ecrireFenetreLog("Suppresion de la classe " 
				     + listeClasse.getSelectedItem());		    
		    // Demande de suppression
		    //alertDictionnaryActionListeners(
                    //            DictionnaryActionEvent.DELETE_CLASS,
		    //		listeClasse.getSelectedItem());
		    zoneIcar.deleteClass(listeClasse.getSelectedItem());
		}
	    }

        );

	// Ouverture du menu
	listeClasse.addMouseListener( new MouseListener () {
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent mouseevent) {
		    
		    // Popup = Clic du bouton droit
		    if((mouseevent.getButton() == MouseEvent.BUTTON3)
		       && (listeClasse.getSelectedItem()!= null)) {	
			// Affiche le menu
			listeClassePopup.show(mouseevent.getComponent(), 
					      mouseevent.getX(), 
					      mouseevent.getY());
		    }
		    
		}
	    }
        );

	listeClasse.add(listeClassePopup);
    }

    // Creer le menu PopUp de gestion des listes de classes/trace
    private PopupMenu creerPopupMenu(ActionListener listener) {

	// Cree le menu
	PopupMenu menuPop = new PopupMenu();


	  // Bouton supprime
	  MenuItem menuItemSupprimer = new MenuItem("Supprimer");
	  menuItemSupprimer.addActionListener(listener);
	  menuPop.add(menuItemSupprimer);
	  



	return menuPop;
    }

    // Creer l'ensemble de la barre de menu
    private void creerBarreMenu() {


	final Frame frame = this;
	MenuBar barreMenu = new MenuBar();
	// Associe le menu a la frame
	this.setMenuBar(barreMenu);
	
	// Menu fichier
	Menu menuFichier = new Menu("Fichier");
	barreMenu.add(menuFichier);

	  // Bouton Nouveau dictionnaire (NEW)
	  MenuItem menuItemNouveauDico = new MenuItem("Nouveau");
	  menuFichier.add(menuItemNouveauDico);
	  menuItemNouveauDico.addActionListener( new ActionListener () {
		  public void actionPerformed(ActionEvent event)
		  {
		      // Demande la creation d'un nouveau dictionnaire
		      //alertDictionnaryActionListeners(
		      //		  DictionnaryActionEvent.NEW_DICTIONNARY,
		      //	  null);
		      zoneIcar.newDictionnary();
		      // Efface la fenetre de dessin
		      zoneIcar.clear();
		      modifierZoneTexte("");

		  }

	      });

	  // Bouton Ouvrir dictionnaire (OPEN)
	  MenuItem menuItemChargerDico = new MenuItem("Charger...");
	  menuFichier.add(menuItemChargerDico);
	  menuItemChargerDico.addActionListener( new ActionListener () {
		  public void actionPerformed(ActionEvent event)
		  {
		      // Creation de la boite de dialogue
		      FileDialog fenetreFichier = 
			  new FileDialog(frame, 
					 "Charger dictionnaire", 
					 FileDialog.LOAD);

		      fenetreFichier.setFilenameFilter( new FilenameFilter () {
			      public boolean accept(File dir, String name) {
				  return (name.matches("(.)*dat"));
			      }

			  } );

		      // Affichage
		      fenetreFichier.setFile("*.dat");
		      fenetreFichier.show();

		      // Charge le fichier selectionne
		      if (fenetreFichier.getFile() != null) {
			  if (zoneIcar.loadFromFile(fenetreFichier.getDirectory()
						    + fenetreFichier.getFile()))
			      // Log
			      ecrireFenetreLog("Chargement du dictionnaire \"" 
					       + fenetreFichier.getFile()
					       + "\"");
			  else
			      // Log
			      ecrireFenetreLog("Chargement du dictionnaire \"" 
					       + fenetreFichier.getFile()
					       + "\" impossible");
			  //alertDictionnaryActionListeners(
			  //	     DictionnaryActionEvent.LOAD_DICTIONNARY,
			  //	     fenetreFichier.getFile());
		      };

		      // Efface la fenetre de dessin
		      zoneIcar.clear();
		      modifierZoneTexte("");
		  }
	      }	     
					     );

	  // Bouton Sauver dictionnaire (SAVE)
	  MenuItem menuItemSauverDico = new MenuItem("Sauver...");
	  menuFichier.add(menuItemSauverDico);
	  menuItemSauverDico.addActionListener( new ActionListener () {
		  public void actionPerformed(ActionEvent event)
		  {
		      // Creation de la boite de dialogue
		      FileDialog fenetreFichier = 
			  new FileDialog(frame, 
					 "Sauver dictionnaire", 
					 FileDialog.SAVE);

		      fenetreFichier.setFilenameFilter( new FilenameFilter () {
			      public boolean accept(File dir, String name) {
				  return (name.matches("(.)*dat"));
			      }

			  } );

		      // Filtrage de l'affichage
		      fenetreFichier.setFile("*.dat");

		      // Affichage
		      fenetreFichier.show();

		      // Sauve le fichier selectionne
		      if (fenetreFichier.getFile() != null) {
			  zoneIcar.saveToFile(fenetreFichier.getDirectory()
					      +fenetreFichier.getFile());

			  // Log
			  ecrireFenetreLog("Sauvegarde du dictionnaire \"" 
					     + fenetreFichier.getFile()
					     + "\"");
			  
			  //alertDictionnaryActionListeners(
			  //	     DictionnaryActionEvent.SAVE_DICTIONNARY,
			  //	     fenetreFichier.getFile());
		      }
		  }
	      }	     
					     );

	  // Separateur
	  MenuItem menuItemSeparateur = new MenuItem("-");
	  menuFichier.add(menuItemSeparateur);

	  // Bouton quitter
	  MenuItem menuItemQuitter = new MenuItem("Quitter");
	  menuFichier.add(menuItemQuitter);
	  menuItemQuitter.addActionListener( new ActionListener () {
		  public void actionPerformed(ActionEvent event)
		  {
		      System.exit(0);
		  }
	      }	     
					     );


	// Menu Option
	Menu menuOption = new Menu("Options");
	barreMenu.add(menuOption);

	  // Bouton Detail du trace
	  CheckboxMenuItem menuItemDetail = 
	      new CheckboxMenuItem("Details des traces");
	  menuItemDetail.setState(true);
	  // Les évenements sont transmis a la zone de dessin
	  menuItemDetail.addItemListener( new ItemListener() {   
		  // Case d'apprentissage cochée
		  public void itemStateChanged(ItemEvent e)
		  { 
		      // Choix "avec ou sans details du trace"
		      if (e.getItemSelectable() instanceof CheckboxMenuItem) {
			  // Change l'etat
			  zoneIcar.switchShowDetailsState();
		      }
		  }

	      }
					  );
	  menuOption.add(menuItemDetail);

	  // Bouton reglage echantillonage
	  MenuItem menuItemEchantillonage = new MenuItem("Regler l'echantillonage...");
	  menuOption.add(menuItemEchantillonage);
	  menuItemEchantillonage.addActionListener( new ActionListener () {
		  public void actionPerformed(ActionEvent event)
		  {

		      // Afficher le dialoge de reglage de l'échantillonage
		      DialogEchantillonage fenetreEchantillonage = 
			  new DialogEchantillonage(frame, 
						   "Entrez la valeur de l'échantillonage du trace:",
						   zoneIcar.getEchantillonage());
		      fenetreEchantillonage.setEditable(echantillonageCanBeModified);
		      fenetreEchantillonage.show();

		      // Recupere la valeur rentree au clavier
		      long echantillonage = fenetreEchantillonage.getValeur();

		      // Fixe la valeur dans la valeur dans la zone de trace
		      if (echantillonage > 0) {
			  zoneIcar.setEchantillonage((int)echantillonage);
		      
			  int precision = TimePrecision.getMinPrecision();
			  // Averti l'utilisateur qu'il a rentre une mauvaise valeur
			  if (echantillonage < precision) {
			      ecrireFenetreLog("!! Attention !! l'echantillonage"
					       + "que vous avez selectionne "
					       + "n'est pas supporte par votre "
					       + "systeme (< " 
					       + precision
					       + "ms)");
			  }
		      }

		  }

	      }
						    );


    }


    // Selectionne une classe dans la liste des classes
    private void selectClasse(String motif) {
	for (int k=0; k<listeClasse.getItemCount(); k++) {
	    if (listeClasse.getItem(k).equals(motif)) {
		listeClasse.select(k);

		break;
	    }
	}
    }

    // METHODES PUBLIQUES

    // Modifie le texte de la zone de texte
    public void modifierZoneTexte(String texte) {
	super.modifierZoneTexte(texte);
	selectClasse(texte);

    }

    // Ajouter un message dans la fenetre de log
    public void ecrireFenetreLog(String texte) {
	Calendar date = Calendar.getInstance();
	// Insere le texte dans la fenetre de log suivi 
	// d'un retour de ligne à la ligne 0
	fenetreLog.insert( "["+date.get(Calendar.HOUR_OF_DAY ) 
			   + ":" + date.get(Calendar.MINUTE)
			   + ":" + date.get(Calendar.SECOND) + "]"
			   + " " + texte + "\n",
			   0);

    } 

    // Met a jour la liste visuelle des trace d'une classe
    // L'argument est un Vector de Vector de Point
    public void majListeTrace(Vector listeTrace) {
	
	// Efface l'ancien contenu
	listeTraces.clear();

	// Liste vide?
	if (listeTrace == null) return;

	// Parcour la liste trace
	for (int k=0; k <listeTrace.size(); k++) {
	    listeTraces.add(new IcarPreview(40,40, 
				    ((Vector)listeTrace.elementAt(k))));
	    
	}
	pack();

    }

    // Met a jour la liste visuelle des classes connnue par le systeme
    public void majListeClasse(Set listeClasse) {

	// Sauvegarde de l'item selectionne
	//String classSauv = this.listeClasse.getSelectedItem();
	String classSauv = champTexte.getText();

	// Efface le contenu précédent
	this.listeClasse.removeAll();

	// Indique si il n'y a aucun classe
	if (listeClasse.size() <= 0) {
	    this.listeClasse.add("Aucune classe");
	    echantillonageCanBeModified = true;
	} else {
	    //Rend impossible la modification de l'echantillonage
	    echantillonageCanBeModified = false;
	}

	// Iterateur de parcour de la liste
	Iterator iter = listeClasse.iterator();

	// Parcour la liste et ajoute chaque élement au conteneur graphique
	while(iter.hasNext()) {
	    // Element suivant
	    String classe = (String)iter.next();

	    // Ajout de l'item dans la liste
	    this.listeClasse.add(classe);

	    // Tente de reselectionner le précedent element selectionne
	    if (classe.equals(classSauv) && (classSauv != null)) {
		this.listeClasse.select(this.listeClasse.getItemCount()-1);
	    }
	}



    }

    // EVENEMENTS

    // Evenements de l'interface
    public void itemStateChanged(ItemEvent e)
    { 	
	//super.itemStateChanged(e);
 
	// Case "apprentissage"
	if (e.getItemSelectable() instanceof Checkbox) {
	    // Change l'etat
	    isTraining ^= true;
	    zoneIcar.setTrainingMode(isTraining);
	    // Log
	    ecrireFenetreLog("Mode apprentissage "+
			     (isTraining?"active":"desactive"));
	} else 
	// Liste des classe selectionee
	if (e.getItemSelectable() instanceof List) {
	    // Mise à jour de la liste des trace par rapport
	    // a la classe selectionee, par demande au listeners
	    majListeTrace(zoneIcar.getListeTrace(listeClasse.getSelectedItem()));
	    
	    // On fixe le champ de texte au motif de la classe
	    modifierZoneTexte(listeClasse.getSelectedItem());
	} 
	// Un trace a ete slectionne dans la liste des traces
	else if (e.getItemSelectable() instanceof ComponentScrollList) {
	    // On affiche le trace selectionne dans la zone de dessin
	    zoneIcar.afficher(((IcarPreview)e.getItem()).getTrace());

	}

    }


    // --IcarRapportClasseEvent
    
    // Cette methode est appelé lorsqu'un évenement se produit
    public void processIcarRapportClasseEvent(IcarRapportClasseEvent event) {
	majListeClasse(event.getListeClasse());
    }

    // --IcarRapportTraceEvent

    // Cette methode est appelé lorsqu'un évenement se produit
    public void processIcarRapportTraceEvent(IcarRapportTraceEvent event) {
	majListeTrace(event.getListeTrace());
    }

}
