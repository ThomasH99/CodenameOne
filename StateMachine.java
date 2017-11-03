/**
 * Your application code goes here
 */

package userclasses;

import com.codename1.io.Log;
import generated.StateMachineBase;
import com.codename1.ui.*; 
import com.codename1.ui.events.*;
import com.codename1.ui.spinner.TimeSpinner;
//import com.todocatalyst.todocat.EditField;
//import com.todocatalyst.todocat.EditFieldDate;
import com.todocatalyst.todocat.Item;
//import com.todocatalyst.todocat.Log;

/**
 *
 * @author Your name here
 */
public class StateMachine extends StateMachineBase {
    public StateMachine(String resFile) {
        super(resFile);
        // do not modify, write code in initVars and initialize class members there,
        // the constructor might be invoked too late due to race conditions that might occur
    }
    /**
     * allows you to subclass a specific component type, e.g. if you want to subclass all the components to type X you can do that easily. This will preserve all property settings as well naturally.
     * @param componentType
     * @param cls
     * @return 
     */
    
    protected Component createComponentInstance(String componentType, Class cls) {
        Log.p("createComponentInstance: componentType="+componentType+" cls="+cls);
        if ("EditField".equals(componentType)) {
//            if (cls.equals(obj))
//            return new EditField(Item.FIDESCRIPTION, null);
            return null;
        }
        return null;
    }
//    protected Component createComponentInstance(String componentType, Class cls) {
//        return null;
//    }
    
    /**
     * this method should be used to initialize variables instead of
     * the constructor/class scope to avoid race conditions
     */
    protected void initVars() {
    }


    protected boolean onGUI1Cmn1() {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        boolean val = super.onGUI1Cmn1();
        
        return val;
    }

    protected boolean onGUI1C3() {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        boolean val = super.onGUI1C3();
        
        return val;
    }

    protected void exitGUI1(Form f) {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        super.exitGUI1(f);
    
    }

    protected void postGUI1(Form f) {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        super.postGUI1(f);
    
    }

    protected boolean onGUI1C7() {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        boolean val = super.onGUI1C7();
        
        return val;
    }

    protected void onCreateGUI1() {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        super.onCreateGUI1();
    
    }

    protected void beforeGUI1(Form f) {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        super.beforeGUI1(f);
    
    }

    protected void onCreateItemScreen() {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        super.onCreateItemScreen();
    
    }

    void replaceStyle(Component oldComp, Component newComp) {
                newComp.setUnselectedStyle(oldComp.getUnselectedStyle());
        newComp.setSelectedStyle(oldComp.getSelectedStyle());
        newComp.setPressedStyle(oldComp.getPressedStyle());
    }
    
    void replaceComponents(Container cont) {
        Component c;
        Component n;
        for (int i=0, size=cont.getComponentCount(); i<size;i++) {
            if ((c=cont.getComponentAt(i)).getUIID().equals("duration")) {
//                n= new EditField(new Integer(0));
//                replaceStyle(c,n);
//                c.getParent().replace(c, n, null);
            } else if (c instanceof Container) {
                replaceComponents((Container)c);
            }
        }
    }
    
    protected void beforeItemScreen(Form f) {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        super.beforeItemScreen(f);
        Component c = findTestLabel1(f);
//        c.getParent().replace(c, new EditField(EditField.INTERRUPT, new Boolean(false)), null);
//        Component n = new TimeSpinner();
//        n.setUnselectedStyle(c.getUnselectedStyle());
//        n.setSelectedStyle(c.getSelectedStyle());
//        n.setPressedStyle(c.getPressedStyle());
//        c.getParent().replace(c, new TimeSpinner(), null);
        replaceComponents(f);
        
    if(Display.getInstance().getDeviceDensity() <= Display.DENSITY_LOW) {
        
    } else {
        
    }
//    findTimeSliderPosition(cmp.getParent()).setText();
    }

    protected void beforeTestScreen(Form f) {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        super.beforeTestScreen(f);
        findTestButton(f).setText("button text set programmatically");
        Component c = findTestLabel(f);
//        c.getParent().replace(c, new EditField(EditField.PRIORITY_IMPORTANCE, null), null);
//        findTestLabel(f).setText("label text set programmatically");
    }

    protected void onTestScreen_TestButtonAction(Component c, ActionEvent event) {
        // If the resource file changes the names of components this call will break notifying you that you should fix the code
        super.onTestScreen_TestButtonAction(c, event);
        showForm("ItemScreen", null);
//        findItemScreen(c.getParent()).show();
    
    }
}
