package com.arvind.linebalancing.utilities;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.arvind.linebalancing.dao.ConnectionTableDao;
import com.arvind.linebalancing.dao.DesignationTableDao;
import com.arvind.linebalancing.dao.EmployeeMachineMapTableDao;
import com.arvind.linebalancing.dao.EmployeeOperationMapTableDao;
import com.arvind.linebalancing.dao.EmployeeRatingTableDao;
import com.arvind.linebalancing.dao.EmployeeTableDao;
import com.arvind.linebalancing.dao.LineEfficiencyTableDao;
import com.arvind.linebalancing.dao.LineEmployeeMapTableDao;
import com.arvind.linebalancing.dao.LineExecutiveMapTableDao;
import com.arvind.linebalancing.dao.LineSupervisorMapTableDao;
import com.arvind.linebalancing.dao.LineTableDao;
import com.arvind.linebalancing.dao.MachineOperationMapTableDao;
import com.arvind.linebalancing.dao.MachineTableDao;
import com.arvind.linebalancing.dao.OperationTableDao;
import com.arvind.linebalancing.dao.StitchTableDao;
import com.arvind.linebalancing.dao.SuggestionTableDao;
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

@Database(entities = {
        ConnectionTable.class,
        DesignationTable.class,
        EmployeeMachineMapTable.class,
        EmployeeOperationMapTable.class,
        EmployeeRatingTable.class,
        EmployeeTable.class,
        LineEmployeeMapTable.class,
        LineEfficiencyTable.class,
        LineExecutiveMapTable.class,
        LineSupervisorMapTable.class,
        LineTable.class,
        MachineOperationMapTable.class,
        MachineTable.class,
        OperationTable.class,
        StitchTable.class,
        SuggestionTable.class},
        version = 1, exportSchema =  false) // 8 is to be kept
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();

    private static AppDatabase appDatabase;

    public abstract ConnectionTableDao connectionTableDao();

    public abstract DesignationTableDao designationTableDao();

    public abstract EmployeeMachineMapTableDao employeeMachineMapTableDao();

    public abstract EmployeeOperationMapTableDao employeeOperationMapTableDao();

    public abstract EmployeeRatingTableDao employeeRatingTableDao();

    public abstract EmployeeTableDao employeeTableDao();

    public abstract LineEmployeeMapTableDao lineEmployeeMapTableDao();

    public abstract LineEfficiencyTableDao lineEfficiencyTableDao();

    public abstract LineExecutiveMapTableDao lineExecutiveMapTableDao();

    public abstract LineSupervisorMapTableDao lineSupervisorMapTableDao();

    public abstract LineTableDao lineTableDao();

    public abstract MachineOperationMapTableDao machineOperationMapTableDao();

    public abstract MachineTableDao machineTableDao();

    public abstract OperationTableDao operationTableDao();

    public abstract StitchTableDao stitchTableDao();

    public abstract SuggestionTableDao suggestionTableDao();

    private Context context;

    public static AppDatabase getInstance(Context context) {

        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "AdminDatabase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;

    }

//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//
//            Log.d(TAG, "migrate: ");
//
//        }
//    };
//
//    public static void destroyInstance() {
//        appDatabase = null;
//    }

}