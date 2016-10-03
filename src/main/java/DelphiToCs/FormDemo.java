package DelphiToCs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Created by Даша on 01.10.2016.
 */

public class FormDemo {

    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private final DataDemo dataDemo;

    @FXML
    private javafx.scene.control.TextField march;
    @FXML
    private javafx.scene.control.TextField june;
    @FXML
    private javafx.scene.control.TextField september;
    @FXML
    private javafx.scene.control.TextField december;
    @FXML
    private javafx.scene.control.TextField yearEdit;
    @FXML
    private javafx.scene.control.TextField phone;
    @FXML
    private javafx.scene.control.TextField street;
    @FXML
    private javafx.scene.control.DatePicker datePicker;
    @FXML
    private Button buttonOk;
    @FXML
    private TableView table;
    @FXML
    private javafx.scene.chart.LineChart chart;

    @Autowired
    public FormDemo(DataDemo dataDemo) {
        this.dataDemo = dataDemo;
    }

    @FXML
    private void initialize() {
        try {
            ResultSet set = dataDemo.executeQuery("select * from DemoTable");
            if (set.next()) {
                phone.setText(set.getString("phone"));
                street.setText(set.getString("street"));
                LocalDate date = LocalDate.now();
                datePicker.setValue(date);
                ResultSet set2 = dataDemo.executeQuery("select * from DemoValues Order by year DESC");
                initData(set2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonUpdate() {
        chart.setTitle(datePicker.getValue().toString());
        try {
            ResultSet set = dataDemo.executeQuery("select * from DemoValues Order by year DESC");
            if (set.next()) {
                fillTable(set);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onTableAction() {
        try {
            chart.getData().retainAll();
            Integer index = table.getSelectionModel().getFocusedIndex();
            ResultSet set = dataDemo.executeQuery("select * from DemoValues Order by year DESC");
            int i = 0;
            while (set.next()) {
                if (i++ == index) {
                    XYChart.Series series = new XYChart.Series();
                    series.getData().add(new XYChart.Data("march",
                            Float.parseFloat(set.getString("march"))));
                    series.getData().add(new XYChart.Data("june",
                            Float.parseFloat(set.getString("june"))));
                    series.getData().add(new XYChart.Data("september",
                            Float.parseFloat(set.getString("september"))));
                    series.getData().add(new XYChart.Data("december",
                            Float.parseFloat(set.getString("december"))));
                    chart.getData().add(series);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData(ResultSet set) {
        try {
            for (int i = 0; i < set.getMetaData().getColumnCount() - 1; i++) {
                final int j = i;
                TableColumn col = new TableColumn(set.getMetaData().getColumnName(i + 2));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                table.getColumns().addAll(col);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillTable(ResultSet res) {
        try {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(res.getString("Year"));
            row.add(res.getString("march"));
            row.add(res.getString("june"));
            row.add(res.getString("september"));
            row.add(res.getString("december"));
            data.add(row);
            while (res.next()) {
                ObservableList<String> row2 = FXCollections.observableArrayList();
                row2.add(res.getString("Year"));
                row2.add(res.getString("march"));
                row2.add(res.getString("june"));
                row2.add(res.getString("september"));
                row2.add(res.getString("december"));
                data.add(row2);
            }
            table.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonOkClick() {
        dataDemo.closeConnection();
        Stage stage = (Stage) buttonOk.getScene().getWindow();
        stage.close();
    }

    private void checkInformation(String message) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        Button butt = new Button("Ok.");
        butt.setOnAction((ActionEvent e) -> stage.close());
        stage.setScene(new Scene(VBoxBuilder.create().children(new Text(message), butt).
                alignment(Pos.CENTER).padding(new Insets(5)).build()));
        stage.show();
    }

    @FXML
    public void buttonFindClick() {
        if (!yearEdit.getText().isEmpty()) {
            try {
                ResultSet set = dataDemo.executeQuery("select * from DemoValues Where year = " + yearEdit.getText());
                if (set.next()) {
                    march.setText(set.getString("march"));
                    june.setText(set.getString("june"));
                    september.setText(set.getString("september"));
                    december.setText(set.getString("december"));
                }else buttonClearClick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            checkInformation("Please check the information.");
        }
    }

    @FXML
    public void buttonClearClick() {
        yearEdit.setText("");
        march.setText("");
        june.setText("");
        september.setText("");
        december.setText("");
    }

    @FXML
    public void buttonAddClick() {
        if (!yearEdit.getText().isEmpty() && !june.getText().isEmpty() && !march.getText().isEmpty() && !december.getText().isEmpty() && !september.getText().isEmpty()) {
            try {
                dataDemo.executeUpdate("Insert into DemoValues values(" + maxId() + "," + yearEdit.getText() + "," + march.getText() + "," + june.getText() + "," + september.getText() + "," + december.getText() + ")");
                checkInformation("The record is added.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else checkInformation("Please check the information.");
    }

    @FXML
    public void buttonDeleteClick() {
        if (!yearEdit.getText().isEmpty()) {
            try {
                dataDemo.executeUpdate("Delete * From DemoValues Where year = " + yearEdit.getText());
                checkInformation("The record is deleted.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            checkInformation("Please check the information.");
        }
    }

    @FXML
    public void buttonUpdateClick() {
        if (!yearEdit.getText().isEmpty() && !june.getText().isEmpty() && !march.getText().isEmpty() && !december.getText().isEmpty() && !september.getText().isEmpty()) {
            try {
                dataDemo.executeUpdate("Update  DemoValues  Set march = " + march.getText() + ",june=" + june.getText() + ",september=" + september.getText() + ",december=" + december.getText() + " Where Year = " + yearEdit.getText());
                checkInformation("The record is updated.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else checkInformation("Please check the information.");
    }

    private Integer maxId() {
        try {
            return Integer.parseInt(dataDemo.executeQuery("Select Max(id) as id From DemoValues").getString("id")) + 1;
        } catch (Exception e) {
        }
        return null;
    }
}
