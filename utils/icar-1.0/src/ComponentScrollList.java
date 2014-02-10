import java.awt.Component;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Color;
import java.awt.Image;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;
import java.awt.ItemSelectable;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.AWTEvent;
import java.util.Vector;
import java.util.Iterator;

/**
 * Cette classe est un containeur AWT à ascenseur pouvant contenir n'importe 
 * quel composant AWT graphique et proposant des moyens de selection 
 * des objets contenus. Un évenement est envoyé lorsqu'un nouvel objet 
 * est selectionné.
 *
 * @author SALUT Jerome
 */
public class ComponentScrollList extends ScrollPane 
    implements MouseListener, ItemSelectable {

    // CONSTANTES
    private static final int largeurEncadrement = 1;

    // ATTRIBUTS

    // Le conteneur des composants
    private Panel panel;

    // La liste des listener pour l'evenement ItemSelectable
    private Vector listeItemListeners;

    // L'object selectionne et sa couleur
    private Component objetSelected;
    private Color couleurSelected;

    // CONSTRUCTEURS
    /**
     * Cree un composant vide.
     *
     * @param largeur la largeur en pixel voulue
     * @param nbCols le nombre de colonne maximum à l'intérieur du composant
     */
    public ComponentScrollList(int largeur, int nbCols) {

	// Creer la zone scrollable par appel du constructeur de la
	// super classe
	super(ScrollPane.SCROLLBARS_AS_NEEDED);

	// Aucun object selectionne
	objetSelected = null;
	
	// Aucun listeners
	listeItemListeners = new Vector();

	// Cree le panel
	panel = new Panel();
	panel.setLayout(new GridLayout(0,nbCols, 
				       largeurEncadrement,largeurEncadrement  ));

	// Ajoute le panel a this
	super.add(panel);

	// Retaille le scrollPane en tenant compte des espacements et de la 
	// largueur de l'ascenseur

	setSize(largeur 
		+ nbCols * largeurEncadrement*2 
		+ 16 // ici je fixe a 16 car getHScrollbarHeight() renvoi 0 ...
		,100);
    }

    // METHODES PRIVEES
    private void encadreObjetSelectionne(Color couleur) {

	// Recupere un contexe graphique sur le panel
	Graphics g = panel.getGraphics();
	// Recupere la position de l'objet selectionne
	Point p = objetSelected.getLocation();

	// Dessiner un rectangle encadrant le composant selectionne
	g.setColor(couleur);
	g.drawRect((int)(p.getX()) - largeurEncadrement,
		   (int)(p.getY()) - largeurEncadrement,
		   (int)(objetSelected.getWidth()) + largeurEncadrement, 
		   (int)(objetSelected.getHeight()) + largeurEncadrement);
    }


    // METHODES PUBLIQUES
    /*
    public void paint(Graphics g) {
	
	if (objetSelected != null) {

	    encadreObjetSelectionne(Color.green);
	    
	}
    }
    */
 
    /**
     * Ajouter un composant
     *
     * @param composant le composant à ajouter
     * @return a value of type 'Component'
     */
    public Component add(Component composant) {

	// Ajoute le composant au panel
	panel.add(composant);

	// S'enregistre en tant que listener des evenements souris sur
	// ce composant (pour la selection)
	composant.addMouseListener(this);

	// Redessine
	panel.repaint();

	return composant;
    }
 
    /**
     * Retourne l'objet courant sélectionné.
     *
     * @return le composant selectionné
     */
    public Component getSelectedItem() {
	return objetSelected;
    }

    /**
     * Vide le composant de son contenu
     *
     */
    public void clear() {
	panel.removeAll();
    }

    // EVENEMENTS
    
    // Invoque lors du clic sur un composant du panel
    public void mouseClicked(MouseEvent e) {
	super.processMouseEvent(e);
    }

    public void mouseEntered(MouseEvent e) {
	super.processMouseEvent(e);

    }

    public void mouseExited(MouseEvent e) {
	super.processMouseEvent(e);

    }

    public void mousePressed(MouseEvent e) {
	// Laisse l'evenement se propager a la classe mere;
	super.processMouseEvent(e);

	// Restaure l'ancienne couleur de l'objet anciennement selectionne
	// si necessaire
	if (objetSelected != null) {
	    objetSelected.setBackground(couleurSelected);
	    encadreObjetSelectionne(panel.getBackground());
	}


	// Enregistre l'object qui a provoque l'evenement
	// c'est a dire l'object selectionne
	objetSelected = e.getComponent();
	couleurSelected = objetSelected.getBackground();
	
	// Marque le nouvel objet selectionne
	objetSelected.setBackground(Color.lightGray);
	encadreObjetSelectionne(Color.black);

	// Previent les listeners
	processEvent();

    }

    public void mouseReleased(MouseEvent e) {
	super.processMouseEvent(e);
    }

    // EVENEMENT ItemSelectable (nouvel objet selectionne)

    /**
     * Ajoute un listener (nouvel objet selectionné)
     *
     * @param l a value of type 'ItemListener'
     */
    public void addItemListener(ItemListener l) {
	listeItemListeners.add(l);
    }

    /**
     * Supprime un listener (nouvel objet selectionné)
     *
     * @param l a value of type 'ItemListener'
     */
    public void removeItemListener(ItemListener l) {
	listeItemListeners.remove(l);
    }
 
    /**
     * Retourne la liste des objets selectionnes (ici un seul)
     *
     */
    public Object[]getSelectedObjects() {
	Object [] r = new Object[1];
	r[0] = getSelectedItem();
	return (r);
    }

    // 
    /**
     * Envoie l'évenement d'un nouvel objet selectionné. Ne pas appeler
     * directement.
     *
     */
    protected void processEvent() {
	// Un iterateur pour parcourir la liste des listeners
	Iterator iter = listeItemListeners.iterator();

	// L'evenement a transmettre
	ItemEvent event = 
	    new ItemEvent(this, 0, 
			  getSelectedItem(), ItemEvent.SELECTED);

	// Parcour la liste des listeners et leur envoi l'evenement event
	while(iter.hasNext()) {
	    ItemListener listener = (ItemListener)iter.next();
	    listener.itemStateChanged(event) ;
	}
    }
}
