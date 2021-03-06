package org.uengine.kernel.face;

import org.metaworks.Face;
import org.metaworks.MetaworksContext;
import org.metaworks.annotation.AutowiredFromClient;
import org.metaworks.annotation.Payload;
import org.metaworks.annotation.ServiceMethod;
import org.metaworks.component.SelectBox;
import org.uengine.contexts.MappingTree;
import org.uengine.kernel.Role;
import org.uengine.kernel.bpmn.face.RolePanel;
import org.uengine.modeling.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryuha on 2015-06-11.
 */
public class RoleSelectorFace extends SelectBox implements Face <Role>{

//    @AutowiredFromClient
//    public RolePanel rolePanel;

    @AutowiredFromClient(payload = "elementViewList[__className=='org.uengine.kernel.view.RoleView'].element.name")
    public Canvas canvas;


    @Override
    public void setValueToFace(Role value) {


        if(canvas != null){
            List<Role> roleList = MappingTree.parseRoles(canvas);

            if(roleList !=null) {
                ArrayList<String> options = new ArrayList<String>();

                for (Role role : roleList) {
                    options.add(role.getName());
                }

                setOptionNames(options);
                setOptionValues(options);
            }
        }

        if(value != null) {
            if(getOptionNames() == null || getOptionNames().size() == 0){
                ArrayList<String> options = new ArrayList<String>();
                options.add(value.getName());
                setOptionNames(options);
                setOptionValues(options);
            }
            setSelectedValue(value.getName());
        }

        setMetaworksContext(new MetaworksContext());
        getMetaworksContext().setWhen(MetaworksContext.WHEN_EDIT);
    }

    @Override
    public Role createValueFromFace() {
        String roleName = getSelectedText();

        return Role.forName(roleName);
    }

    @ServiceMethod(inContextMenu = true)
    public void loadOptions(@Payload("selected") String selected){

        setValueToFace(Role.forName(selected));
    }
}
