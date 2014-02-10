import java.lang.Integer;
import java.awt.Dialog;
import java.awt.Label;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.TextField;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import icar.util.TimePrecision;

/**
 * Cette classe est une boite de dialogue permettant le reglage de
 * la valeur d'échantillonage des tracés.
 *
 * @author SALUT Jerome
 */
public class DialogEchantillonage extends Dialog {
    // ATTRIBUTS

    // Zone de saisie de texte
    private TextField champTexte;

    // Bouton tester la precision
    private Button buttonTester;

    // Bouton ok
    private Button ok;

    // CONSTRUCTEURS

    /**
     * Cree la boite de dialogue.
     *
     * @param parent la frame parent
     * @param message le message indiquant l'opération à effectuer
     */
    public DialogEchantillonage(Frame parent, String message) {

	// Appel du constructeur de la classe parent
	super(parent, true);

	final Dialog dialogue = this;

	setTitle("Reglage de l'echantillonage");
	setLayout(new BorderLayout());
	setResizable(false);

	// Le message d'invite
	this.add(new Label(message), BorderLayout.NORTH);

	// Le champ de saisie du haut
	champTexte = new TextField("", 4);
	this.add(champTexte, BorderLayout.WEST);
	this.add(new Label("ms"), BorderLayout.CENTER);

	// Bouton valeur optimale
	buttonTester = new Button("Valeur optimale sur cet Os");
	this.add(buttonTester, BorderLayout.EAST);
	// L'action du bouton est de determiner l'échantillonage minimum
	buttonTester.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent evt) {

		    // Fixe la zone de saisie avec la bonne valeur
		    champTexte.setText( "" + getMinEchantillonage());
		    
		}
	    }			       
					);


	// Le corps du la fenetre
	Panel p = new Panel();
	p.setLayout(new FlowLayout());

	// Bouton ok
	ok = new Button("Ok");
	ok.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    // Ferme le dialogue
		    dialogue.hide();
		    dialogue.dispose();
		}
	    }
			      );
	p.add(ok);

	// Bouton annuler
	Button annuler = new Button("Annuler");
	annuler.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    // Ne renvoie rien
		    champTexte.setText("");
		    // Ferme le dialogue
		    dialogue.hide();
		    dialogue.dispose();
		}
	    }
			      );
	p.add(annuler);

	// Ajoute le panel au dialogue
	this.add(p, BorderLayout.SOUTH);


	this.setSize(300,100);
	this.centrer();
	this.pack();
  
    }  

    /**
     * Cree la boite de dialogue avec une valeur par defaut
     *
     * @param parent la frame parent
     * @param message le message indiquant l'opération à effectuer
     * @param valeurInitiale la valeur de l'échantillonage par defaut
     */
    public DialogEchantillonage(Frame parent, String message, int valeurInitiale) {
	this(parent, message);
	champTexte.setText("" + valeurInitiale);

    };

    // METHODES PUBLIQUES

    /**
     * Renvoie la valeur entrée dans la zone de saisie
     *
     * @return a value of type 'int'
     */
    public int getValeur() {
	if (!champTexte.getText().equals("")) {
	    int resultat = Integer.parseInt(champTexte.getText());
	    //if (resultat < getMinEchantillonage())
	    return (resultat);
	} else {
	    // -1 = annuler
	    return -1;
	}
    }

    /**
     * Determine si zone de saisie peut etre modifiee ou non
     *
     * @param editable a value of type 'boolean'
     */
    public void setEditable(boolean editable) {
	champTexte.setEnabled(editable);
	buttonTester.setEnabled(editable);
	ok.setEnabled(editable);
    }

    // METHODES PRIVEES

    // Renvoie la valeur d'echantillonage la plus petite sur ce systeme
    private int getMinEchantillonage() {

	return TimePrecision.getMinPrecision();

    }
  
    // Centre le dialoge sur l'écran
    private void centrer() {
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();;
	setLocation( (d.width-getWidth())/2 , 
		     (d.height-getHeight())/2);

    }
}
