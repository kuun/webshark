package org.webshark.control;


import javafx.scene.control.Skin;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FlowTableView<T> extends TableView<T> {
    private static final Logger log = LoggerFactory.getLogger(FlowTableView.class);

    private static class FlowTableViewSkin<T> extends TableViewSkin<T> {
        public FlowTableViewSkin(TableView<T> control) {
            super(control);
        }


        @Override
        protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            double height = 24;
            var flow = getVirtualFlow();

            log.debug("flow height: {}", flow.getHeight());
            for (int i = 0; i < getItemCount(); i++) {
                height += flow.getCell(i).getHeight();
            }
            return height + snappedTopInset() + snappedBottomInset();
            //return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        /**
         * Reflectively invokes protected getCellLength(i) of flow.
         * @param index the index of the cell.
         * @return the cell height of the cell at index.
         */
        protected double invokeFlowCellLength(VirtualFlow flow, int index) {
            double height = 1.0;
            Class<?> clazz = VirtualFlow.class;
            try {
                Method method = clazz.getDeclaredMethod("getCellLength", Integer.TYPE);
                method.setAccessible(true);
                return ((double) method.invoke(flow, index));
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return height;
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new FlowTableViewSkin<T>(this);
    }
}
