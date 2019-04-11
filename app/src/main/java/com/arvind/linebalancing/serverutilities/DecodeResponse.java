package com.arvind.linebalancing.serverutilities;

import android.content.Context;

import com.arvind.linebalancing.table.ConnectionTable;
import com.arvind.linebalancing.table.DesignationTable;
import com.arvind.linebalancing.table.EmployeeMachineMapTable;
import com.arvind.linebalancing.table.EmployeeOperationMapTable;
import com.arvind.linebalancing.table.EmployeeRatingTable;
import com.arvind.linebalancing.table.EmployeeTable;
import com.arvind.linebalancing.table.LineEfficiencyTable;
import com.arvind.linebalancing.table.LineEmployeeMapTable;
import com.arvind.linebalancing.table.LineExecutiveMapTable;
import com.arvind.linebalancing.table.LineSupervisorMapTable;
import com.arvind.linebalancing.table.LineTable;
import com.arvind.linebalancing.table.MachineOperationMapTable;
import com.arvind.linebalancing.table.MachineTable;
import com.arvind.linebalancing.table.OperationTable;
import com.arvind.linebalancing.table.StitchTable;
import com.arvind.linebalancing.table.SuggestionTable;
import com.arvind.linebalancing.utilities.AppDatabase;
import com.arvind.linebalancing.utilities.Constants;
import com.arvind.linebalancing.utilities.UtilityFile;

import org.json.JSONObject;

public class DecodeResponse {

    private static final String TAG = DecodeResponse.class.getSimpleName();

    private Context context;

    private UtilityFile utilityFile;

    public DecodeResponse(Context context) {

        this.context = context;

        utilityFile = new UtilityFile(context);

    }

    public void decodeAdminObject(JSONObject adminJSONObject) {

        String sId = adminJSONObject.optString("id");
        String sNo = adminJSONObject.optString("employeeNo");
        String sEmail = adminJSONObject.optString("employeeEmail");
        String sEmployeeName = adminJSONObject.optString("employeeName");
        String sDesignationId = adminJSONObject.optString("designationId");
        String sUpdatedAt = adminJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(sUpdatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        EmployeeTable employeeTable = new EmployeeTable();
        employeeTable.setId(sId);
        employeeTable.setDesignationId(sDesignationId);
        employeeTable.setEmployeeName(sEmployeeName);
        employeeTable.setEmployeeNo(sNo);
        employeeTable.setEmployeeEmail(sEmail);
        employeeTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).employeeTableDao().insert(employeeTable);

    }

    public void decodeDesignationObject(JSONObject designationJSONObject) {

        String sId = designationJSONObject.optString("id");
        String sName = designationJSONObject.optString("designationName");
        String sUpdatedAt = designationJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(sUpdatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        DesignationTable designationTable = new DesignationTable();
        designationTable.setDesignationName(sName);
        designationTable.setId(sId);
        designationTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).designationTableDao().insert(designationTable);

    }

    public void decodeEmployeeObject(JSONObject employeeJSONObject) {

        String sId = employeeJSONObject.optString("id");
        String sEmployeeNo = employeeJSONObject.optString("employeeNo");
        String sEmployeeName = employeeJSONObject.optString("employeeName");
        String sEmployeeEmail = employeeJSONObject.optString("employeeEmail");
        String sDesignationId = employeeJSONObject.optString("designationId");
        String sUpdatedAt = employeeJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(sUpdatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        EmployeeTable employeeTable = new EmployeeTable();
        employeeTable.setId(sId);
        employeeTable.setEmployeeEmail(sEmployeeEmail);
        employeeTable.setEmployeeNo(sEmployeeNo);
        employeeTable.setEmployeeName(sEmployeeName);
        employeeTable.setDesignationId(sDesignationId);
        employeeTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).employeeTableDao().insert(employeeTable);
    }

    public void decodeEmployeeRatingObject(JSONObject employeeRatingJSONObject) {

        String sId = employeeRatingJSONObject.optString("id");
        String sEmployeeId = employeeRatingJSONObject.optString("employeeId");
        String sOperationId = employeeRatingJSONObject.optString("operationId");
        String sSamValue = employeeRatingJSONObject.optString("rating");
        String sUpdatedAt = employeeRatingJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(sUpdatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        long lRatingTimestamp = 0;
        if (utilityFile.doesStringExist(employeeRatingJSONObject.optString("ratingTimestamp"))) {
            lRatingTimestamp = (long) Double.parseDouble(employeeRatingJSONObject.optString("ratingTimestamp"));
        }

        EmployeeRatingTable employeeRatingTable = new EmployeeRatingTable();
        employeeRatingTable.setId(sId);
        employeeRatingTable.setEmployeeId(sEmployeeId);
        employeeRatingTable.setOperationId(sOperationId);
        employeeRatingTable.setRatingTimestamp(lRatingTimestamp);
        employeeRatingTable.setSamValue(Double.parseDouble(sSamValue));
        employeeRatingTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).employeeRatingTableDao().insert(employeeRatingTable);
    }

    public void decodeLineObject(JSONObject lineJSONObject) {

        String sId = lineJSONObject.optString("id");
        String sLineNo = lineJSONObject.optString("lineNo");
        String sLineName = lineJSONObject.optString("lineName");
        String sUpdatedAt = lineJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(sUpdatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        LineTable lineTable = new LineTable();

        lineTable.setId(sId);
        lineTable.setLineName(sLineName);
        lineTable.setLineNo(sLineNo);
        lineTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).lineTableDao().insert(lineTable);

    }

    public void decodeMachineObject(JSONObject machineJSONObject) {

        String sId = machineJSONObject.optString("id");
        String sMachineName = machineJSONObject.optString("machineName");
        String sAssignedName = machineJSONObject.optString("assignedName");
        String sShortName = machineJSONObject.optString("shortName");
        String sMachineIndex = machineJSONObject.optString("machineIndex");
        String sUpdatedAt = machineJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(sUpdatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        MachineTable machineTable = new MachineTable();
        machineTable.setId(sId);
        machineTable.setAssignedName(sAssignedName);
        machineTable.setMachineName(sMachineName);
        machineTable.setShortName(sShortName);
        machineTable.setMachineIndex(sMachineIndex);
        machineTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).machineTableDao().insert(machineTable);

    }

    public void decodeOperationObject(JSONObject operationJSONObject) {

        String sId = operationJSONObject.optString("id");
        String sOperationName = operationJSONObject.optString("operationName");
        String sSamValue = operationJSONObject.optString("samValue");
        String sUpdatedAt = operationJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(sUpdatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        OperationTable operationTable = new OperationTable();
        operationTable.setId(sId);
        operationTable.setOperationName(sOperationName);
        operationTable.setSamValue(Double.parseDouble(sSamValue));
        operationTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).operationTableDao().insert(operationTable);

    }

    public void decodeStitchObject(JSONObject stitchJSONObject) {

        String sId = stitchJSONObject.optString("id");
        String sLineName = stitchJSONObject.optString("stitchName");
        String sUpdatedAt = stitchJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(sUpdatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        StitchTable stitchTable = new StitchTable();
        stitchTable.setId(sId);
        stitchTable.setStitchName(sLineName);
        stitchTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).stitchTableDao().insert(stitchTable);

    }

    public void decodeEmployeeMachineMapObject(JSONObject employeeMachineMapJSONObject) {

        String sId = employeeMachineMapJSONObject.optString("id");
        String machineId = employeeMachineMapJSONObject.optString("machineId");
        String employeeId = employeeMachineMapJSONObject.optString("employeeId");
        String updatedAt = employeeMachineMapJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(updatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        EmployeeMachineMapTable employeeMachineMapTable = new EmployeeMachineMapTable();
        employeeMachineMapTable.setId(sId);
        employeeMachineMapTable.setEmployeeId(employeeId);
        employeeMachineMapTable.setMachineId(machineId);
        employeeMachineMapTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).employeeMachineMapTableDao().insert(employeeMachineMapTable);

    }

    public void decodeMachineOperationMapObject(JSONObject machineOperationMapJSONObject) {

        String sId = machineOperationMapJSONObject.optString("id");
        String machineId = machineOperationMapJSONObject.optString("machineId");
        String operationId = machineOperationMapJSONObject.optString("operationId");
        String updatedAt = machineOperationMapJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(updatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        MachineOperationMapTable machineOperationMapTable = new MachineOperationMapTable();
        machineOperationMapTable.setId(sId);
        machineOperationMapTable.setOperationId(operationId);
        machineOperationMapTable.setMachineId(machineId);
        machineOperationMapTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).machineOperationMapTableDao().insert(machineOperationMapTable);

    }

    public void decodeEmployeeOperationMapObject(JSONObject employeeOperationMapJSONObject) {

        String sId = employeeOperationMapJSONObject.optString("id");
        String employeeId = employeeOperationMapJSONObject.optString("employeeId");
        String operationId = employeeOperationMapJSONObject.optString("operationId");
        String updatedAt = employeeOperationMapJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(updatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        EmployeeOperationMapTable employeeOperationMapTable = new EmployeeOperationMapTable();
        employeeOperationMapTable.setId(sId);
        employeeOperationMapTable.setEmployeeId(employeeId);
        employeeOperationMapTable.setOperationId(operationId);
        employeeOperationMapTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).employeeOperationMapTableDao().insert(employeeOperationMapTable);

    }

    public void decodeLineExecutiveMapObject(JSONObject lineExecutiveMapJSONObject) {

        String sId = lineExecutiveMapJSONObject.optString("id");
        String lineId = lineExecutiveMapJSONObject.optString("lineId");
        String employeeId = lineExecutiveMapJSONObject.optString("employeeId");
        String updatedAt = lineExecutiveMapJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(updatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        LineExecutiveMapTable lineExecutiveMapTable = new LineExecutiveMapTable();
        lineExecutiveMapTable.setId(sId);
        lineExecutiveMapTable.setEmployeeId(employeeId);
        lineExecutiveMapTable.setLineId(lineId);
        lineExecutiveMapTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).lineExecutiveMapTableDao().insert(lineExecutiveMapTable);

    }

    public void decodeLineEmployeeMapObject(JSONObject lineEmployeeMapJSONObject) {

        String sId = lineEmployeeMapJSONObject.optString("id");
        String lineId = lineEmployeeMapJSONObject.optString("lineId");
        String employeeId = lineEmployeeMapJSONObject.optString("employeeId");
        String updatedAt = lineEmployeeMapJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(updatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        LineEmployeeMapTable lineEmployeeMapTable = new LineEmployeeMapTable();
        lineEmployeeMapTable.setId(sId);
        lineEmployeeMapTable.setEmployeeId(employeeId);
        lineEmployeeMapTable.setLineId(lineId);
        lineEmployeeMapTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).lineEmployeeMapTableDao().insert(lineEmployeeMapTable);

    }

    public void decodeLineSupervisorMapObject(JSONObject lineSupervisorMapJSONObject) {

        String sId = lineSupervisorMapJSONObject.optString("id");
        String lineId = lineSupervisorMapJSONObject.optString("lineId");
        String employeeId = lineSupervisorMapJSONObject.optString("employeeId");
        String updatedAt = lineSupervisorMapJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(updatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        LineSupervisorMapTable lineSupervisorMapTable = new LineSupervisorMapTable();
        lineSupervisorMapTable.setId(sId);
        lineSupervisorMapTable.setEmployeeId(employeeId);
        lineSupervisorMapTable.setLineId(lineId);
        lineSupervisorMapTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).lineSupervisorMapTableDao().insert(lineSupervisorMapTable);

    }

    public void decodeLineEfficiencyObject(JSONObject lineEfficiencyJSONObject) {

        String sId = lineEfficiencyJSONObject.optString("id");
        String lineId = lineEfficiencyJSONObject.optString("lineId");
        String lineEfficiency = lineEfficiencyJSONObject.optString("lineEfficiency");
        String updatedAt = lineEfficiencyJSONObject.optString("updatedAt");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(updatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        LineEfficiencyTable lineEfficiencyTable = new LineEfficiencyTable();
        lineEfficiencyTable.setId(sId);
        lineEfficiencyTable.setLineEfficiency(Double.parseDouble(lineEfficiency));
        lineEfficiencyTable.setLineId(lineId);
        lineEfficiencyTable.setUpdatedAt(lUpdateAt);

        AppDatabase.getInstance(context).lineEfficiencyTableDao().insert(lineEfficiencyTable);

    }

    public void decodeConnectionObject(JSONObject connectionJSONObject) {

        String sId = connectionJSONObject.optString("id");
        String lineId = connectionJSONObject.optString("lineId");
        String machineId = connectionJSONObject.optString("machineId");
        String operationId = connectionJSONObject.optString("operationId");
        String employeeId = connectionJSONObject.optString("employeeId");
        String updatedAt = connectionJSONObject.optString("updatedAt");

        long lTimestamp = 0;
        if (utilityFile.doesStringExist(connectionJSONObject.optString("timestamp"))) {
            lTimestamp = (long) Double.parseDouble(connectionJSONObject.optString("timestamp"));
        }

        long lUpdateAt = utilityFile.getTimestampFromStringTime(updatedAt, Constants.DATE_TIME_FORMAT_MYSQL);

        ConnectionTable connectionTable = new ConnectionTable();
        connectionTable.setId(sId);
        connectionTable.setUpdatedAt(lUpdateAt);
        connectionTable.setTimestamp(lTimestamp);
        connectionTable.setOperationId(operationId);
        connectionTable.setMachineId(machineId);
        connectionTable.setEmployeeId(employeeId);
        connectionTable.setLineId(lineId);

        AppDatabase.getInstance(context).connectionTableDao().insert(connectionTable);

    }

    public void decodeSuggestionObject(JSONObject suggestionJSONObject) {

        String sId = suggestionJSONObject.optString("id");
        String lineId = suggestionJSONObject.optString("lineId");
        String machineId = suggestionJSONObject.optString("machineId");
        String operationId = suggestionJSONObject.optString("absentOperationId");
        String assignedEmployeeId = suggestionJSONObject.optString("assignedEmployeeId");
        String absentEmployeeId = suggestionJSONObject.optString("absentEmployeeId");
        String lineEfficiency = suggestionJSONObject.optString("lineEfficiency");
        String updatedAt = suggestionJSONObject.optString("updatedAt");
        String timestamp = suggestionJSONObject.optString("timestamp");

        JSONObject line = suggestionJSONObject.optJSONObject("line");
        lineId = line.optString("lineName");

        JSONObject machine = suggestionJSONObject.optJSONObject("machine");
        machineId = machine.optString("machineName");

        JSONObject absentEmployee = suggestionJSONObject.optJSONObject("absentEmployee");
        absentEmployeeId = absentEmployee.optString("employeeName");

        JSONObject assignedEmployee = suggestionJSONObject.optJSONObject("assignedEmployee");
        assignedEmployeeId = assignedEmployee.optString("employeeName");

        JSONObject absentOperation = suggestionJSONObject.optJSONObject("absentOperation");
        operationId = absentOperation.optString("operationName");

        long lUpdateAt = utilityFile.getTimestampFromStringTime(updatedAt, Constants.DATE_TIME_FORMAT_MYSQL);
        long lTimestamp = Long.parseLong(timestamp);

        SuggestionTable suggestionTable = new SuggestionTable();
        suggestionTable.setId(sId);
        suggestionTable.setLineId(lineId);
        suggestionTable.setMachineId(machineId);
        suggestionTable.setTimestamp(lTimestamp);
        suggestionTable.setUpdatedAt(lUpdateAt);
        suggestionTable.setOperationId(operationId);
        suggestionTable.setLineEfficiency(Double.parseDouble(lineEfficiency));
        suggestionTable.setAssignedEmployeeId(assignedEmployeeId);
        suggestionTable.setAbsentEmployeeId(absentEmployeeId);

        AppDatabase.getInstance(context).suggestionTableDao().insert(suggestionTable);

    }
}
