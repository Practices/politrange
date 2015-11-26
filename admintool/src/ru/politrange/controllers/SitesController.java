package ru.politrange.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.politrange.interfaces.impls.CollectionSitesCatalog;
import ru.politrange.objects.Site;
import ru.politrange.utils.DialogManager;

import java.io.IOException;

/**
 * Created by developermsv on 24.11.2015.
 */
public class SitesController {
    private CollectionSitesCatalog sitesCatalogImpl = new CollectionSitesCatalog();
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private Parent fxmlEdit;
    private EditSiteController editSiteController;
    private Stage mainStage;
    private Stage editSiteStage;

    @FXML
    public TableColumn<Site, String> columnName;
    public TableView mainTable;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    private void initialize() {
        columnName.setCellValueFactory(new PropertyValueFactory<Site, String>("name"));
        initListeners();
        fillData();
        initLoader();
    }

    // #good_code_4 методы не пергружены логикой
    // инициализация слушателей по таблице интерфейса
    private void initListeners() {
        mainTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == MainController.DOUBLE_CLICK) {
                    editSite();
                }
            }
        });
    }

    // заполнение таблицы интерфейса
    private void fillData() {
        sitesCatalogImpl.fillTestData();
        mainTable.setItems(sitesCatalogImpl.getSiteList());
    }

    // предзагрузка интерфейса редактирования, чтобы не загружать при каждом нажатии на кнопки
    private void initLoader() {
        try {

            fxmlLoader.setLocation(getClass().getResource("../fxml/edit_site.fxml"));
            fxmlEdit = fxmlLoader.load();
            editSiteController = fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // #good_code_5 функция выполняет одну задачу
    // обработка событий нажатия на кнопки
    public void actionButtonPressed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        // если нажата не кнопка - выходим из метода
        if (!(source instanceof Button)) {
            return;
        }
        Site selectedSite = (Site) mainTable.getSelectionModel().getSelectedItem();
        Button clickedButton = (Button) source;

        switch (clickedButton.getId()) {
            case "btnAdd":
                editSiteController.setSite(new Site());
                showDialog(MainController.TEXT_TITLE_ADD);
                sitesCatalogImpl.add(editSiteController.getSite());
                break;

            case "btnEdit":
                if (siteIsSelected(selectedSite)) {
                    editSite();
                }
                break;

            case "btnDelete":
                if (siteIsSelected(selectedSite)) {
                    if (DialogManager.showConfirmDialog(MainController.TEXT_WARNING, MainController.TEXT_CONFIRM +
                            selectedSite.getName() + "\"?")) {
                        sitesCatalogImpl.delete(selectedSite);
                    }
                }
                break;
        }
    }
    private boolean siteIsSelected(Site selectedSite) {
        if(selectedSite == null){
            DialogManager.showInfoDialog(MainController.TEXT_ERROR, MainController.TEXT_SELECT_RECORD);
            return false;
        }
        return true;
    }

    //#good_code_8 не повторять код
    // редактировать запись
    private void editSite() {
        editSiteController.setSite((Site) mainTable.getSelectionModel().getSelectedItem());
        showDialog(MainController.TEXT_TITLE_EDIT);
    }

    // отображение диалога редактирования
    private void showDialog(String title) {

        if (editSiteStage == null) {
            editSiteStage = new Stage();
            editSiteStage.setTitle(title);
            editSiteStage.setResizable(false);
            editSiteStage.setScene(new Scene(fxmlEdit));
            editSiteStage.initModality(Modality.WINDOW_MODAL);
            editSiteStage.initOwner(mainStage);
        }
        editSiteStage.showAndWait(); // для ожидания закрытия окна
    }
}
