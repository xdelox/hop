/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.pipeline.transforms.processfiles;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ProcessFilesDialog extends BaseTransformDialog {
  private static final Class<?> PKG = ProcessFilesMeta.class; // For Translator

  private CCombo wSourceFileNameField;

  private Label wlTargetFileNameField;
  private CCombo wTargetFileNameField;

  private Button wAddResult;
  private Label wlAddResult;

  private Button wOverwriteTarget;
  private Label wlOverwriteTarget;

  private Button wCreateParentFolder;
  private Label wlCreateParentFolder;

  private Button wSimulate;

  private final ProcessFilesMeta input;

  private CCombo wOperation;

  private boolean gotPreviousFields = false;

  public ProcessFilesDialog(
      Shell parent,
      IVariables variables,
      ProcessFilesMeta transformMeta,
      PipelineMeta pipelineMeta) {
    super(parent, variables, transformMeta, pipelineMeta);
    input = transformMeta;
  }

  @Override
  public String open() {
    Shell parent = getParent();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
    PropsUi.setLook(shell);
    setShellImage(shell, input);

    ModifyListener lsMod = e -> input.setChanged();

    SelectionAdapter lsButtonChanged =
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            input.setChanged();
          }
        };

    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = PropsUi.getFormMargin();
    formLayout.marginHeight = PropsUi.getFormMargin();

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "ProcessFilesDialog.Shell.Title"));

    int middle = props.getMiddlePct();
    int margin = PropsUi.getMargin();

    // TransformName line
    wlTransformName = new Label(shell, SWT.RIGHT);
    wlTransformName.setText(BaseMessages.getString(PKG, "ProcessFilesDialog.TransformName.Label"));
    PropsUi.setLook(wlTransformName);
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment(0, 0);
    fdlTransformName.right = new FormAttachment(middle, -margin);
    fdlTransformName.top = new FormAttachment(0, margin);
    wlTransformName.setLayoutData(fdlTransformName);
    wTransformName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wTransformName.setText(transformName);
    PropsUi.setLook(wTransformName);
    wTransformName.addModifyListener(lsMod);
    fdTransformName = new FormData();
    fdTransformName.left = new FormAttachment(middle, 0);
    fdTransformName.top = new FormAttachment(0, margin);
    fdTransformName.right = new FormAttachment(100, 0);
    wTransformName.setLayoutData(fdTransformName);

    // ///////////////////////////////
    // START OF Settings GROUP //
    // ///////////////////////////////

    Group wSettingsGroup = new Group(shell, SWT.SHADOW_NONE);
    PropsUi.setLook(wSettingsGroup);
    wSettingsGroup.setText(BaseMessages.getString(PKG, "ProcessFilesDialog.wSettingsGroup.Label"));

    FormLayout settingGroupLayout = new FormLayout();
    settingGroupLayout.marginWidth = 10;
    settingGroupLayout.marginHeight = 10;
    wSettingsGroup.setLayout(settingGroupLayout);

    // Operation
    Label wlOperation = new Label(wSettingsGroup, SWT.RIGHT);
    wlOperation.setText(BaseMessages.getString(PKG, "ProcessFilesDialog.Operation.Label"));
    PropsUi.setLook(wlOperation);
    FormData fdlOperation = new FormData();
    fdlOperation.left = new FormAttachment(0, 0);
    fdlOperation.right = new FormAttachment(middle, -margin);
    fdlOperation.top = new FormAttachment(wTransformName, margin);
    wlOperation.setLayoutData(fdlOperation);

    wOperation = new CCombo(wSettingsGroup, SWT.BORDER | SWT.READ_ONLY);
    PropsUi.setLook(wOperation);
    wOperation.addModifyListener(lsMod);
    FormData fdOperation = new FormData();
    fdOperation.left = new FormAttachment(middle, 0);
    fdOperation.top = new FormAttachment(wTransformName, margin);
    fdOperation.right = new FormAttachment(100, -margin);
    wOperation.setLayoutData(fdOperation);
    wOperation.setItems(ProcessFilesMeta.operationTypeDesc);
    wOperation.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            updateOperation();
          }
        });

    // Create target parent folder?
    wlCreateParentFolder = new Label(wSettingsGroup, SWT.RIGHT);
    wlCreateParentFolder.setText(
        BaseMessages.getString(PKG, "ProcessFilesDialog.CreateParentFolder.Label"));
    PropsUi.setLook(wlCreateParentFolder);
    FormData fdlCreateParentFolder = new FormData();
    fdlCreateParentFolder.left = new FormAttachment(0, 0);
    fdlCreateParentFolder.top = new FormAttachment(wOperation, margin);
    fdlCreateParentFolder.right = new FormAttachment(middle, -margin);
    wlCreateParentFolder.setLayoutData(fdlCreateParentFolder);
    wCreateParentFolder = new Button(wSettingsGroup, SWT.CHECK);
    PropsUi.setLook(wCreateParentFolder);
    wCreateParentFolder.setToolTipText(
        BaseMessages.getString(PKG, "ProcessFilesDialog.CreateParentFolder.Tooltip"));
    wCreateParentFolder.addSelectionListener(lsButtonChanged);
    FormData fdCreateParentFolder = new FormData();
    fdCreateParentFolder.left = new FormAttachment(middle, 0);
    fdCreateParentFolder.top = new FormAttachment(wlCreateParentFolder, 0, SWT.CENTER);
    wCreateParentFolder.setLayoutData(fdCreateParentFolder);

    // Overwrite target file?
    wlOverwriteTarget = new Label(wSettingsGroup, SWT.RIGHT);
    wlOverwriteTarget.setText(
        BaseMessages.getString(PKG, "ProcessFilesDialog.OverwriteTarget.Label"));
    PropsUi.setLook(wlOverwriteTarget);
    FormData fdlOverwriteTarget = new FormData();
    fdlOverwriteTarget.left = new FormAttachment(0, 0);
    fdlOverwriteTarget.top = new FormAttachment(wCreateParentFolder, margin);
    fdlOverwriteTarget.right = new FormAttachment(middle, -margin);
    wlOverwriteTarget.setLayoutData(fdlOverwriteTarget);
    wOverwriteTarget = new Button(wSettingsGroup, SWT.CHECK);
    PropsUi.setLook(wOverwriteTarget);
    wOverwriteTarget.setToolTipText(
        BaseMessages.getString(PKG, "ProcessFilesDialog.OverwriteTarget.Tooltip"));
    wOverwriteTarget.addSelectionListener(lsButtonChanged);
    FormData fdOverwriteTarget = new FormData();
    fdOverwriteTarget.left = new FormAttachment(middle, 0);
    fdOverwriteTarget.top = new FormAttachment(wlOverwriteTarget, 0, SWT.CENTER);
    wOverwriteTarget.setLayoutData(fdOverwriteTarget);

    // Add Target filename to result filenames?
    wlAddResult = new Label(wSettingsGroup, SWT.RIGHT);
    wlAddResult.setText(BaseMessages.getString(PKG, "ProcessFilesDialog.AddResult.Label"));
    PropsUi.setLook(wlAddResult);
    FormData fdlAddResult = new FormData();
    fdlAddResult.left = new FormAttachment(0, 0);
    fdlAddResult.top = new FormAttachment(wOverwriteTarget, margin);
    fdlAddResult.right = new FormAttachment(middle, -margin);
    wlAddResult.setLayoutData(fdlAddResult);
    wAddResult = new Button(wSettingsGroup, SWT.CHECK);
    PropsUi.setLook(wAddResult);
    wAddResult.setToolTipText(BaseMessages.getString(PKG, "ProcessFilesDialog.AddResult.Tooltip"));
    wAddResult.addSelectionListener(lsButtonChanged);
    FormData fdAddResult = new FormData();
    fdAddResult.left = new FormAttachment(middle, 0);
    fdAddResult.top = new FormAttachment(wlAddResult, 0, SWT.CENTER);
    wAddResult.setLayoutData(fdAddResult);

    // Simulation mode ON?
    Label wlSimulate = new Label(wSettingsGroup, SWT.RIGHT);
    wlSimulate.setText(BaseMessages.getString(PKG, "ProcessFilesDialog.Simulate.Label"));
    PropsUi.setLook(wlSimulate);
    FormData fdlSimulate = new FormData();
    fdlSimulate.left = new FormAttachment(0, 0);
    fdlSimulate.top = new FormAttachment(wAddResult, margin);
    fdlSimulate.right = new FormAttachment(middle, -margin);
    wlSimulate.setLayoutData(fdlSimulate);
    wSimulate = new Button(wSettingsGroup, SWT.CHECK);
    PropsUi.setLook(wSimulate);
    wSimulate.setToolTipText(BaseMessages.getString(PKG, "ProcessFilesDialog.Simulate.Tooltip"));
    wSimulate.addSelectionListener(lsButtonChanged);
    FormData fdSimulate = new FormData();
    fdSimulate.left = new FormAttachment(middle, 0);
    fdSimulate.top = new FormAttachment(wlSimulate, 0, SWT.CENTER);
    wSimulate.setLayoutData(fdSimulate);

    FormData fdSettingsGroup = new FormData();
    fdSettingsGroup.left = new FormAttachment(0, margin);
    fdSettingsGroup.top = new FormAttachment(wTransformName, margin);
    fdSettingsGroup.right = new FormAttachment(100, -margin);
    wSettingsGroup.setLayoutData(fdSettingsGroup);

    // ///////////////////////////////
    // END OF Settings Fields GROUP //
    // ///////////////////////////////

    // SourceFileNameField field
    Label wlSourceFileNameField = new Label(shell, SWT.RIGHT);
    wlSourceFileNameField.setText(
        BaseMessages.getString(PKG, "ProcessFilesDialog.SourceFileNameField.Label"));
    PropsUi.setLook(wlSourceFileNameField);
    FormData fdlSourceFileNameField = new FormData();
    fdlSourceFileNameField.left = new FormAttachment(0, 0);
    fdlSourceFileNameField.right = new FormAttachment(middle, -margin);
    fdlSourceFileNameField.top = new FormAttachment(wSettingsGroup, 2 * margin);
    wlSourceFileNameField.setLayoutData(fdlSourceFileNameField);

    wSourceFileNameField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
    PropsUi.setLook(wSourceFileNameField);
    wSourceFileNameField.setEditable(true);
    wSourceFileNameField.addModifyListener(lsMod);
    FormData fdSourceFileNameField = new FormData();
    fdSourceFileNameField.left = new FormAttachment(middle, 0);
    fdSourceFileNameField.top = new FormAttachment(wSettingsGroup, 2 * margin);
    fdSourceFileNameField.right = new FormAttachment(100, -margin);
    wSourceFileNameField.setLayoutData(fdSourceFileNameField);
    wSourceFileNameField.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // Disable focusLost event
          }

          @Override
          public void focusGained(FocusEvent e) {
            Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
            shell.setCursor(busy);
            get();
            shell.setCursor(null);
            busy.dispose();
          }
        });
    // TargetFileNameField field
    wlTargetFileNameField = new Label(shell, SWT.RIGHT);
    wlTargetFileNameField.setText(
        BaseMessages.getString(PKG, "ProcessFilesDialog.TargetFileNameField.Label"));
    PropsUi.setLook(wlTargetFileNameField);
    FormData fdlTargetFileNameField = new FormData();
    fdlTargetFileNameField.left = new FormAttachment(0, 0);
    fdlTargetFileNameField.right = new FormAttachment(middle, -margin);
    fdlTargetFileNameField.top = new FormAttachment(wSourceFileNameField, margin);
    wlTargetFileNameField.setLayoutData(fdlTargetFileNameField);

    wTargetFileNameField = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
    wTargetFileNameField.setEditable(true);
    PropsUi.setLook(wTargetFileNameField);
    wTargetFileNameField.addModifyListener(lsMod);
    FormData fdTargetFileNameField = new FormData();
    fdTargetFileNameField.left = new FormAttachment(middle, 0);
    fdTargetFileNameField.top = new FormAttachment(wSourceFileNameField, margin);
    fdTargetFileNameField.right = new FormAttachment(100, -margin);
    wTargetFileNameField.setLayoutData(fdTargetFileNameField);
    wTargetFileNameField.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // Disable focusLost event
          }

          @Override
          public void focusGained(FocusEvent e) {
            Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
            shell.setCursor(busy);
            get();
            shell.setCursor(null);
            busy.dispose();
          }
        });

    // THE BUTTONS
    wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

    setButtonPositions(new Button[] {wOk, wCancel}, margin, wTargetFileNameField);

    // Add listeners
    wOk.addListener(SWT.Selection, e -> ok());
    wCancel.addListener(SWT.Selection, e -> cancel());

    getData();
    updateOperation();
    input.setChanged(changed);

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return transformName;
  }

  private void updateOperation() {
    wlOverwriteTarget.setEnabled(
        ProcessFilesMeta.getOperationTypeByDesc(wOperation.getText())
            != ProcessFilesMeta.OPERATION_TYPE_DELETE);
    wOverwriteTarget.setEnabled(
        ProcessFilesMeta.getOperationTypeByDesc(wOperation.getText())
            != ProcessFilesMeta.OPERATION_TYPE_DELETE);
    wlAddResult.setEnabled(
        ProcessFilesMeta.getOperationTypeByDesc(wOperation.getText())
            != ProcessFilesMeta.OPERATION_TYPE_DELETE);
    wAddResult.setEnabled(
        ProcessFilesMeta.getOperationTypeByDesc(wOperation.getText())
            != ProcessFilesMeta.OPERATION_TYPE_DELETE);
    wlTargetFileNameField.setEnabled(
        ProcessFilesMeta.getOperationTypeByDesc(wOperation.getText())
            != ProcessFilesMeta.OPERATION_TYPE_DELETE);
    wTargetFileNameField.setEnabled(
        ProcessFilesMeta.getOperationTypeByDesc(wOperation.getText())
            != ProcessFilesMeta.OPERATION_TYPE_DELETE);
    wlCreateParentFolder.setEnabled(
        ProcessFilesMeta.getOperationTypeByDesc(wOperation.getText())
            != ProcessFilesMeta.OPERATION_TYPE_DELETE);
    wCreateParentFolder.setEnabled(
        ProcessFilesMeta.getOperationTypeByDesc(wOperation.getText())
            != ProcessFilesMeta.OPERATION_TYPE_DELETE);
  }

  /** Copy information from the meta-data input to the dialog fields. */
  public void getData() {
    if (log.isDebug()) {
      logDebug(BaseMessages.getString(PKG, "ProcessFilesDialog.Log.GettingKeyInfo"));
    }

    if (input.getSourceFilenameField() != null) {
      wSourceFileNameField.setText(input.getSourceFilenameField());
    }
    if (input.getTargetFilenameField() != null) {
      wTargetFileNameField.setText(input.getTargetFilenameField());
    }
    wOperation.setText(ProcessFilesMeta.getOperationTypeDesc(input.getOperationType()));
    wAddResult.setSelection(input.isAddResultFilenames());
    wOverwriteTarget.setSelection(input.isOverwriteTargetFile());
    wCreateParentFolder.setSelection(input.isCreateParentFolder());
    wSimulate.setSelection(input.isSimulate());

    wTransformName.selectAll();
    wTransformName.setFocus();
  }

  private void cancel() {
    transformName = null;
    input.setChanged(changed);
    dispose();
  }

  private void ok() {
    if (Utils.isEmpty(wTransformName.getText())) {
      return;
    }
    input.setSourceFilenameField(wSourceFileNameField.getText());
    input.setTargetFilenameField(wTargetFileNameField.getText());
    input.setOperationType(ProcessFilesMeta.getOperationTypeByDesc(wOperation.getText()));
    input.setAddResultFilenames(wAddResult.getSelection());
    input.setOverwriteTargetFile(wOverwriteTarget.getSelection());
    input.setCreateParentFolder(wCreateParentFolder.getSelection());
    input.setSimulate(wSimulate.getSelection());
    transformName = wTransformName.getText(); // return value

    dispose();
  }

  private void get() {
    if (!gotPreviousFields) {
      gotPreviousFields = true;
      try {
        String source = wSourceFileNameField.getText();
        String target = wTargetFileNameField.getText();

        wSourceFileNameField.removeAll();
        wTargetFileNameField.removeAll();
        IRowMeta r = pipelineMeta.getPrevTransformFields(variables, transformName);
        if (r != null) {
          wSourceFileNameField.setItems(r.getFieldNames());
          wTargetFileNameField.setItems(r.getFieldNames());
          if (source != null) {
            wSourceFileNameField.setText(source);
          }
          if (target != null) {
            wTargetFileNameField.setText(target);
          }
        }
      } catch (HopException ke) {
        new ErrorDialog(
            shell,
            BaseMessages.getString(PKG, "ProcessFilesDialog.FailedToGetFields.DialogTitle"),
            BaseMessages.getString(PKG, "ProcessFilesDialog.FailedToGetFields.DialogMessage"),
            ke);
      }
    }
  }
}
