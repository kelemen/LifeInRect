package org.kelemenattila.rectlife;

import java.awt.GridLayout;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import org.jtrim.cancel.Cancellation;
import org.jtrim.cancel.CancellationController;
import org.jtrim.cancel.CancellationSource;
import org.jtrim.cancel.CancellationToken;
import org.jtrim.concurrent.CancelableTask;
import org.jtrim.concurrent.TaskExecutorService;
import org.jtrim.concurrent.ThreadPoolTaskExecutor;
import org.jtrim.concurrent.UpdateTaskExecutor;
import org.jtrim.swing.concurrent.SwingUpdateTaskExecutor;

/**
 *
 * @author Kelemen Attila
 */
@SuppressWarnings("serial")
public class LifeFrame extends javax.swing.JFrame {
    private static final TaskExecutorService BCKG_EXECUTOR = createExecutor("LifeExecutor", 1);

    private static final int DEFAULT_WORLD_WIDTH = 100;
    private static final int DEFAULT_WORLD_HEIGHT = 100;
    private static final double DEFAULT_GENE_MUTATE_RATE = 0.001;
    private static final double DEFAULT_ACCIDENT_RATE = 0.001;
    private static final long SHOW_IMAGE_STEP_MS = 1000;

    private CancellationController taskCanceler;
    private ImageDisplay[] viewDisplays;
    private volatile EntityWorld currentWorld;

    /**
     * Creates new form LifeFrame
     */
    public LifeFrame() {
        this.taskCanceler = null;
        this.viewDisplays = null;
        this.currentWorld = null;

        initComponents();

        jAccidentRateEdit.setText(Double.toString(DEFAULT_ACCIDENT_RATE));
        jGeneMutateRateEdit.setText(Double.toString(DEFAULT_GENE_MUTATE_RATE));
        jWorldHeightEdit.setText(Integer.toString(DEFAULT_WORLD_HEIGHT));
        jWorldWidthEdit.setText(Integer.toString(DEFAULT_WORLD_WIDTH));
    }

    private static TaskExecutorService createExecutor(String name, int threadCount) {
        ThreadPoolTaskExecutor result = new ThreadPoolTaskExecutor(
                name, threadCount, Integer.MAX_VALUE, 1000, TimeUnit.MILLISECONDS);
        result.dontNeedShutdown();
        return result;
    }

    private void showWorld(EntityWorld.WorldView[] view) {
        if (viewDisplays == null || viewDisplays.length != view.length) {
            viewDisplays = new ImageDisplay[view.length];

            jFeedbackPanel.removeAll();
            jFeedbackPanel.setLayout(new GridLayout(view.length, 1, 0, 5));

            for (int i = 0; i < view.length; i++) {
                viewDisplays[i] = new ImageDisplay();
                JPanel panel = new JPanel(new GridLayout(1, 1, 0, 0));
                panel.setBorder(new TitledBorder(view[i].getCaption()));
                panel.add(viewDisplays[i]);
                jFeedbackPanel.add(panel);
            }

            jFeedbackPanel.revalidate();
            jFeedbackPanel.repaint();
        }

        for (int i = 0; i < view.length; i++) {
            viewDisplays[i].setImage(view[i].getImage());
        }
    }

    private void showWorldView(EntityWorld world, UpdateTaskExecutor executor) {
        final EntityWorld.WorldView[] view = world.viewWorld();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                showWorld(view);
            }
        });
    }

    private void runWorld(
            CancellationToken cancelToken,
            int worldWidth,
            int worldHeight,
            WorldInitializer initializer) {

        UpdateTaskExecutor progressReporter = new SwingUpdateTaskExecutor();
        UpdateTaskExecutor imageReporter = new SwingUpdateTaskExecutor();

        EntityWorld world = new EntityWorld(worldWidth, worldHeight);
        currentWorld = world;
        initializer.initWorld(world);

        showWorldView(world, imageReporter);

        long stepIndex = 0;
        long lastShowTime = System.nanoTime();
        while (!cancelToken.isCanceled()) {
            world.stepWorld();
            stepIndex++;

            if (System.nanoTime() - lastShowTime >= TimeUnit.MILLISECONDS.toNanos(SHOW_IMAGE_STEP_MS)) {
                lastShowTime = System.nanoTime();
                showWorldView(world, imageReporter);
            }

            final long currentStepIndex = stepIndex;
            progressReporter.execute(new Runnable() {
                @Override
                public void run() {
                    jStepCaption.setText("Step: " + currentStepIndex);
                }
            });
        }
    }

    private int readPositiveIntFromEdit(JTextComponent component, String name) {
        String strValue = component.getText().trim();
        try {
            int result = Integer.parseInt(strValue);
            if (result <= 0) {
                throw new NumberFormatException();
            }
            return result;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException(name + " does not contain a valid value: " + strValue);
        }
    }

    private double readProbabilityFromEdit(JTextComponent component, String name) {
        String strValue = component.getText().trim();
        try {
            double result = Double.parseDouble(strValue);
            if (result < 0 || result > 1.0) {
                throw new NumberFormatException();
            }
            return result;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException(name + " must be a number within [0.0, 1.0] instead of " + strValue);
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jStartStopButton = new javax.swing.JButton();
        jStepCaption = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jAccidentRateEdit = new javax.swing.JTextField();
        jApplyRatesButton = new javax.swing.JButton();
        jGeneMutateRateEdit = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jWorldWidthEdit = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jWorldHeightEdit = new javax.swing.JTextField();
        jFeedbackPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Life in Rect");

        jStartStopButton.setText("Start");
        jStartStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStartStopButtonActionPerformed(evt);
            }
        });

        jStepCaption.setText("Step: 0");

        jLabel2.setText("Accident rate:");

        jApplyRatesButton.setText("Apply rates");
        jApplyRatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyRatesButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Gene mutate rate:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jGeneMutateRateEdit)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 84, Short.MAX_VALUE))
                    .addComponent(jAccidentRateEdit)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jApplyRatesButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jGeneMutateRateEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAccidentRateEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jApplyRatesButton)
                .addContainerGap())
        );

        jLabel3.setText("World width:");

        jLabel4.setText("World height:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jWorldWidthEdit)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jWorldHeightEdit))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jWorldWidthEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jWorldHeightEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jStartStopButton))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jStepCaption)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jStepCaption)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jStartStopButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout jFeedbackPanelLayout = new javax.swing.GroupLayout(jFeedbackPanel);
        jFeedbackPanel.setLayout(jFeedbackPanelLayout);
        jFeedbackPanelLayout.setHorizontalGroup(
            jFeedbackPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 353, Short.MAX_VALUE)
        );
        jFeedbackPanelLayout.setVerticalGroup(
            jFeedbackPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFeedbackPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jFeedbackPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jStartStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStartStopButtonActionPerformed
        if (taskCanceler != null) {
            taskCanceler.cancel();
            taskCanceler = null;
            jStartStopButton.setText("Start");
        }
        else {
            final int worldWidth;
            final int worldHeight;
            final double mutateRate;
            final double accidentRate;

            try {
                worldWidth = readPositiveIntFromEdit(jWorldWidthEdit, "World width");
                worldHeight = readPositiveIntFromEdit(jWorldHeightEdit, "World height");
                mutateRate = readProbabilityFromEdit(jGeneMutateRateEdit, "Gene mutate rate");
                accidentRate = readProbabilityFromEdit(jAccidentRateEdit, "Accident rate");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CancellationSource cancelSource = Cancellation.createCancellationSource();
            taskCanceler = cancelSource.getController();
            jStartStopButton.setText("Stop");

            BCKG_EXECUTOR.execute(cancelSource.getToken(), new CancelableTask() {
                @Override
                public void execute(CancellationToken cancelToken) {
                    runWorld(cancelToken, worldWidth, worldHeight, new WorldInitializer() {
                        @Override
                        public void initWorld(EntityWorld world) {
                            world.setAccidentRate(accidentRate);
                            world.setMutateRate(mutateRate);
                        }
                    });
                }
            }, null);
        }
    }//GEN-LAST:event_jStartStopButtonActionPerformed

    private void jApplyRatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyRatesButtonActionPerformed
        EntityWorld world = currentWorld;
        if (world == null) {
            return;
        }

        try {
            double mutateRate = readProbabilityFromEdit(jGeneMutateRateEdit, "Gene mutate rate");
            double accidentRate = readProbabilityFromEdit(jAccidentRateEdit, "Accident rate");

            world.setMutateRate(mutateRate);
            world.setAccidentRate(accidentRate);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jApplyRatesButtonActionPerformed

    private interface WorldInitializer {
        public void initWorld(EntityWorld world);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jAccidentRateEdit;
    private javax.swing.JButton jApplyRatesButton;
    private javax.swing.JPanel jFeedbackPanel;
    private javax.swing.JTextField jGeneMutateRateEdit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton jStartStopButton;
    private javax.swing.JLabel jStepCaption;
    private javax.swing.JTextField jWorldHeightEdit;
    private javax.swing.JTextField jWorldWidthEdit;
    // End of variables declaration//GEN-END:variables
}
