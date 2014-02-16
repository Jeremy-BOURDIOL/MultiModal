/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fusionenginebandb;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JFrame;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import jp.nyatla.nyartoolkit.core.NyARCode;
import jp.nyatla.nyartoolkit.java3d.utils.J3dNyARParam;
import jp.nyatla.nyartoolkit.java3d.utils.NyARMultipleMarkerBehaviorHolder;
import jp.nyatla.nyartoolkit.java3d.utils.NyARMultipleMarkerBehaviorListener;

/**
 *
 * @author Jéjé
 */
public final class TangibleTracker extends JFrame implements NyARMultipleMarkerBehaviorListener {

    private static int PATT_ID = 0;
    
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    // Represente l'objet rectangle ou ellipse a deplacer
    private final String CARCODE_FILE1 = "./Data/patt.hiro";
    // ROUGE
    private final String CARCODE_FILE2 = "./Data/patt.kanji";
    // BLEU
    private final String CARCODE_FILE3 = "./Data/patt.samp1";
    
    private int PATT_HIRO_ID;
    private int PATT_KANJI_ID;
    private int PATT_SAMP1_ID;
    
    private final String PARAM_FILE = "./Data/camera_para.dat";
      
    private NyARMultipleMarkerBehaviorHolder nya_behavior;
    private J3dNyARParam ar_param;
    private final TransformGroup transformGroupbOject;
    private final VirtualUniverse universe;
    private final Locale locale;
    
    private BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0),100.0);
    private final Canvas3D canvas;

    public TangibleTracker() throws Exception {
        NyARCode ar_codes[];
        ar_codes = new NyARCode[3];
        ar_codes[0] = new NyARCode(16, 16);
        ar_codes[0].loadARPattFromFile(CARCODE_FILE1);
        ar_codes[1] = new NyARCode(16, 16);
        ar_codes[1].loadARPattFromFile(CARCODE_FILE2);
        ar_codes[2] = new NyARCode(16, 16);
        ar_codes[2].loadARPattFromFile(CARCODE_FILE3);

        PATT_HIRO_ID = PATT_ID;
        PATT_ID++;
        PATT_KANJI_ID = PATT_ID;
        PATT_ID++;
        PATT_SAMP1_ID = PATT_ID;
        PATT_ID++;

        double marker_width[];
        marker_width = new double[3];
        marker_width[0] = 0.08;
        marker_width[1] = 0.08;
        marker_width[2] = 0.08;


        ar_param = new J3dNyARParam();
        ar_param.loadARParamFromFile(PARAM_FILE);
        ar_param.changeScreenSize(320, 240);
        	
        universe = new VirtualUniverse();
        locale = new Locale(universe);
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        View view = new View();
        ViewPlatform viewPlatform = new ViewPlatform();
        view.attachViewPlatform(viewPlatform);
        view.addCanvas3D(canvas);
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());

        //è¦–ç•Œã�®è¨­å®š(ã‚«ãƒ¡ãƒ©è¨­å®šã�‹ã‚‰å�–å¾—)
        Transform3D camera_3d = ar_param.getCameraTransform();
        view.setCompatibilityModeEnable(true);
        view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
        view.setLeftProjection(camera_3d);
        
        TransformGroup viewGroup = new TransformGroup();
        Transform3D viewTransform = new Transform3D();
        viewTransform.rotY(Math.PI);
        viewTransform.setTranslation(new Vector3d(0.0, 0.0, 0.0));
        viewGroup.setTransform(viewTransform);
        viewGroup.addChild(viewPlatform);
        BranchGroup viewRoot = new BranchGroup();
        viewRoot.addChild(viewGroup);
        locale.addBranchGraph(viewRoot);
        
        Background background = new Background();
        background.setApplicationBounds(bounds);
        background.setImageScaleMode(Background.SCALE_FIT_ALL);
        background.setCapability(Background.ALLOW_IMAGE_WRITE);
        BranchGroup root = new BranchGroup();
        root.addChild(background);
        
        
        transformGroupbOject = new TransformGroup();
        transformGroupbOject.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroupbOject.addChild(new ColorCube());
        root.addChild(transformGroupbOject);
        
        
        nya_behavior = new NyARMultipleMarkerBehaviorHolder(ar_param, 30f, 
                                                            ar_codes, marker_width, 3);
		
        nya_behavior.setTransformGroup(transformGroupbOject, PATT_HIRO_ID);
        nya_behavior.setBackGround(background);
        
        root.addChild(nya_behavior.getBehavior());
        nya_behavior.setUpdateListener(this);
        
        locale.addBranchGraph(root);

        //ã‚¦ã‚¤ãƒ³ãƒ‰ã‚¦ã�®è¨­å®š
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

        this.setVisible(true);
        Insets ins = this.getInsets();
        this.setSize(320 + ins.left + ins.right, 240 + ins.top + ins.bottom);
        this.startCapture();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    

    // Starting the Behaviour
    public void startCapture() throws Exception
    {
            nya_behavior.start();
    }
    
    @Override
    public void onUpdate(int i_markers, javax.media.j3d.Transform3D td) {
        //System.out.println("Marker : " + i_markers );
        Vector3f v = new Vector3f();
        td.get(v);
        switch (i_markers) {
            case 0:
                support.firePropertyChange("LocationChange", null, v);
                break;
            case 1:
                support.firePropertyChange("Color", null, "ROUGE");
                break;
            case 2:
                support.firePropertyChange("Color", null, "BLEU");
                break;
        }
    }
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
    
    @Override
    public void addPropertyChangeListener(String s, PropertyChangeListener listener) {
        support.addPropertyChangeListener(s, listener);
    }
    
    @Override
    public void removePropertyChangeListener(String s, PropertyChangeListener listener) {
        support.removePropertyChangeListener(s, listener);
    }
    
    public static void main(String[] args) {
        try {
                TangibleTracker frame = new TangibleTracker();

                frame.setVisible(true);
                Insets ins = frame.getInsets();
                frame.setSize(320 + ins.left + ins.right, 240 + ins.top + ins.bottom);
                frame.startCapture();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        } catch (Exception e) {
                e.printStackTrace();
        }
    }
    
}
