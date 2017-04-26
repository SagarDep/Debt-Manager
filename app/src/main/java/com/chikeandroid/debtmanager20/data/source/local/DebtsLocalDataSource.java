package com.chikeandroid.debtmanager20.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.chikeandroid.debtmanager20.data.Debt;
import com.chikeandroid.debtmanager20.data.Person;
import com.chikeandroid.debtmanager20.data.PersonDebt;
import com.chikeandroid.debtmanager20.data.source.DebtsDataSource;
import com.chikeandroid.debtmanager20.data.source.local.DebtsPersistenceContract.DebtsEntry;
import com.chikeandroid.debtmanager20.data.source.local.DebtsPersistenceContract.PersonsEntry;
import com.chikeandroid.debtmanager20.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Chike on 3/22/2017.
 */
@Singleton
public class DebtsLocalDataSource implements DebtsDataSource {

    private static final String TAG = "DebtsLocalDataSource";

    private DebtsDbHelper mDebtsDbHelper;

    public DebtsLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDebtsDbHelper = new DebtsDbHelper(context);
    }

    @Override
    public List<PersonDebt> getAllPersonDebts() {

        List<PersonDebt> personDebts = new ArrayList<>();
        SQLiteDatabase db = mDebtsDbHelper.getReadableDatabase();

        String sql = buildGetDebtsQueryByWhere("no");
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_AMOUNT));
                long dateDue = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_DUE));
                long dateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_ENTERED));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_NOTE));
                String personId = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_PERSON_ID));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_STATUS));
                int debtType1 = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_TYPE));
                String entryId = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.ALIAS_DEBT_ID));

                Debt debt = new Debt.Builder(entryId, personId, amount, dateEntered, debtType1, status)
                        .dueDate(dateDue)
                        .note(note)
                        .build();

                String personName = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_NAME));
                String personPhoneNo = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_PHONE_NO));
                String personEntryId = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.ALIAS_PERSON_ID));

                Person person = new Person(personEntryId, personName, personPhoneNo);

                PersonDebt personDebt = new PersonDebt(person, debt);
                personDebts.add(personDebt);
            }
        }
        if(cursor != null) {
            cursor.close();
        }

        db.close();

        if(personDebts.isEmpty()) {
            // return empty list
            return new ArrayList<>();
        }else {
            return personDebts;
        }
    }

    @Override
    public List<PersonDebt> getAllPersonDebtsByType(@NonNull int debtType) {
        checkNotNull(debtType);

        List<PersonDebt> personDebts = new ArrayList<>();
        SQLiteDatabase db = mDebtsDbHelper.getReadableDatabase();

        String sql = buildGetDebtsQueryByWhere(DebtsEntry.TABLE_NAME + "." + DebtsEntry.COLUMN_TYPE);
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(debtType)});

        if(cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_AMOUNT));
                long dateDue = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_DUE));
                long dateEntered = cursor.getLong(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_ENTERED));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_NOTE));
                String personId = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_PERSON_ID));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_STATUS));
                int debtType1 = cursor.getInt(cursor.getColumnIndexOrThrow(DebtsEntry.COLUMN_TYPE));
                String entryId = cursor.getString(cursor.getColumnIndexOrThrow(DebtsEntry.ALIAS_DEBT_ID));

                Debt debt = new Debt.Builder(entryId, personId, amount, dateEntered, debtType1, status)
                        .dueDate(dateDue)
                        .note(note)
                        .build();

                String personName = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_NAME));
                String personPhoneNo = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_PHONE_NO));
                String personEntryId = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.ALIAS_PERSON_ID));

                Person person = new Person(personEntryId, personName, personPhoneNo);

                PersonDebt personDebt = new PersonDebt(person, debt);
                personDebts.add(personDebt);
            }
        }
        if(cursor != null) {
            cursor.close();
        }

        db.close();

        if(personDebts.isEmpty()) {
            // return empty list
            return new ArrayList<>();
        }else {
            return personDebts;
        }
    }

    @Override
    public PersonDebt getPersonDebt(@NonNull String debtId) {

        checkNotNull(debtId);

        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

        String sql = buildGetDebtsQueryByWhere(DebtsEntry.TABLE_NAME + "." + DebtsEntry.COLUMN_ENTRY_ID);
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(debtId)});

        Debt debt = null;
        PersonDebt personDebt = null;

        if(c != null && c.getCount() > 0) {
            c.moveToFirst();

            String personId = c.getString(c.getColumnIndexOrThrow(DebtsEntry.COLUMN_PERSON_ID));
            int status = c.getInt(c.getColumnIndexOrThrow(DebtsEntry.COLUMN_STATUS));
            double amount = c.getDouble(c.getColumnIndexOrThrow(DebtsEntry.COLUMN_AMOUNT));
            long dateDue = c.getLong(c.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_DUE));
            long dateEntered = c.getLong(c.getColumnIndexOrThrow(DebtsEntry.COLUMN_DATE_ENTERED));
            String note = c.getString(c.getColumnIndexOrThrow(DebtsEntry.COLUMN_NOTE));
            int type = c.getInt(c.getColumnIndexOrThrow(DebtsEntry.COLUMN_TYPE));
            String entryId = c.getString(c.getColumnIndexOrThrow(DebtsEntry.ALIAS_DEBT_ID));

            debt = new Debt.Builder(entryId, personId, amount, dateEntered, type, status)
                    .dueDate(dateDue)
                    .note(note)
                    .build();

            String personName = c.getString(c.getColumnIndexOrThrow(PersonsEntry.COLUMN_NAME));
            String personPhoneNo = c.getString(c.getColumnIndexOrThrow(PersonsEntry.COLUMN_PHONE_NO));
            String personEntryId = c.getString(c.getColumnIndexOrThrow(PersonsEntry.ALIAS_PERSON_ID));

            Person person = new Person(personEntryId, personName, personPhoneNo);

            personDebt = new PersonDebt(person, debt);
        }

        if(c != null) {
            c.close();
        }

        db.close();

        return personDebt;
    }

    public String buildGetDebtsQueryByWhere(String where) {

        String COMMA_SEP = ", ";
        String ALIAS = " AS ";
        StringBuilder sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append("SELECT ");
        sqlStringBuilder.append(DebtsEntry.COLUMN_PERSON_ID);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(DebtsEntry.TABLE_NAME + "." + DebtsEntry.COLUMN_ENTRY_ID);
        sqlStringBuilder.append(ALIAS);
        sqlStringBuilder.append(DebtsEntry.ALIAS_DEBT_ID);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(DebtsEntry.COLUMN_AMOUNT);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(DebtsEntry.COLUMN_DATE_DUE);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(DebtsEntry.COLUMN_DATE_ENTERED);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(DebtsEntry.COLUMN_NOTE);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(DebtsEntry.COLUMN_STATUS);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(DebtsEntry.COLUMN_TYPE);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(PersonsEntry.COLUMN_NAME);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(PersonsEntry.TABLE_NAME + "." + PersonsEntry.COLUMN_ENTRY_ID);
        sqlStringBuilder.append(ALIAS);
        sqlStringBuilder.append(PersonsEntry.ALIAS_PERSON_ID);
        sqlStringBuilder.append(COMMA_SEP);
        sqlStringBuilder.append(PersonsEntry.COLUMN_PHONE_NO);
        sqlStringBuilder.append(" FROM ");
        sqlStringBuilder.append(DebtsEntry.TABLE_NAME);
        sqlStringBuilder.append(" INNER JOIN ");
        sqlStringBuilder.append(PersonsEntry.TABLE_NAME);
        sqlStringBuilder.append(" ON ");
        sqlStringBuilder.append(DebtsEntry.TABLE_NAME + "." + DebtsEntry.COLUMN_PERSON_ID);
        sqlStringBuilder.append(" = ");
        sqlStringBuilder.append(PersonsEntry.TABLE_NAME + "." + PersonsEntry.COLUMN_ENTRY_ID);

        if(!where.equals("no")) {
            sqlStringBuilder.append(" WHERE ");
            sqlStringBuilder.append(where + " = ?");
        }

        return sqlStringBuilder.toString();
    }

    @Override
    public void savePersonDebt(@NonNull Debt debt, @NonNull Person person) {
        checkNotNull(debt);
        checkNotNull(person);

        String personId = person.getId();
        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

        if (!personAlreadyExist(person.getPhoneNumber())) {

            ContentValues personValues = new ContentValues();
            personValues.put(PersonsEntry.COLUMN_ENTRY_ID, person.getId());
            personValues.put(PersonsEntry.COLUMN_NAME, person.getFullname());
            personValues.put(PersonsEntry.COLUMN_PHONE_NO, person.getPhoneNumber());
            db.insert(PersonsEntry.TABLE_NAME, null, personValues);

        } else {

            personId = getPersonIdIfAlreadyExist(person.getPhoneNumber());
        }

        ContentValues debtValues = new ContentValues();
        debtValues.put(DebtsEntry.COLUMN_ENTRY_ID, debt.getId());
        debtValues.put(DebtsEntry.COLUMN_AMOUNT, debt.getAmount());
        debtValues.put(DebtsEntry.COLUMN_DATE_DUE, debt.getDueDate());
        debtValues.put(DebtsEntry.COLUMN_DATE_ENTERED, debt.getCreatedDate());
        debtValues.put(DebtsEntry.COLUMN_NOTE, debt.getNote());
        debtValues.put(DebtsEntry.COLUMN_PERSON_ID, personId);
        debtValues.put(DebtsEntry.COLUMN_STATUS, debt.getStatus());
        debtValues.put(DebtsEntry.COLUMN_TYPE, debt.getDebtType());

        db.insert(DebtsEntry.TABLE_NAME, null, debtValues);

        db.close();
    }

    @Override
    public void refreshDebts() {

    }

    @Override
    public void deleteAllPersonDebts() {

        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

        db.delete(DebtsEntry.TABLE_NAME, null, null);

        db.delete(PersonsEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deletePersonDebt(@NonNull PersonDebt personDebt) {

        checkNotNull(personDebt);

        String debtId = personDebt.getDebt().getId();

        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

        db.delete(DebtsEntry.TABLE_NAME, DebtsEntry.COLUMN_ENTRY_ID + "= ?", new String[]{debtId});

        // delete person if he has only one debt
        String personId = personDebt.getPerson().getId();
        if(personHasOneDebt(personId)) {
            deletePerson(personId);
        }

        db.close();
    }

    @Override
    public void deletePerson(@NonNull String personId) {
        checkNotNull(personId);
        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();
        db.delete(PersonsEntry.TABLE_NAME, DebtsEntry.COLUMN_ENTRY_ID + " = ?", new String[]{personId});
        db.close();
    }

    private boolean personHasOneDebt(String personId) {
        checkNotNull(personId);
        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DebtsEntry.TABLE_NAME + " WHERE " +
                DebtsEntry.COLUMN_PERSON_ID  + " = ?", new String[]{personId});

        if(cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void deleteAllPersonDebtsByType(@NonNull int debtType) {
        checkNotNull(debtType);

        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

        db.delete(DebtsEntry.TABLE_NAME, DebtsEntry.COLUMN_TYPE + "= ?", new String[] {String.valueOf(debtType)});

        db.close();
    }

    @Override
    public void updatePersonDebt(@NonNull PersonDebt personDebt) {

        checkNotNull(personDebt);
        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

        ContentValues personContentValues = new ContentValues();
        Person person = personDebt.getPerson();
        personContentValues.put(PersonsEntry.COLUMN_PHONE_NO, person.getPhoneNumber());
        personContentValues.put(PersonsEntry.COLUMN_NAME, person.getFullname());

        Debt debt = personDebt.getDebt();
        String personId;
        if (!personAlreadyExist(person.getPhoneNumber())) {

            personId = saveNewPerson(person);

        } else {

            personId = getPersonIdIfAlreadyExist(person.getPhoneNumber());
            db.update(PersonsEntry.TABLE_NAME, personContentValues, PersonsEntry.COLUMN_ENTRY_ID + " = ?", new String[]{personId});
        }

        ContentValues debtContentValues = new ContentValues();
        debtContentValues.put(DebtsEntry.COLUMN_DATE_ENTERED, debt.getCreatedDate());
        debtContentValues.put(DebtsEntry.COLUMN_PERSON_ID, personId);
        debtContentValues.put(DebtsEntry.COLUMN_DATE_DUE, debt.getDueDate());
        debtContentValues.put(DebtsEntry.COLUMN_NOTE, debt.getNote());
        debtContentValues.put(DebtsEntry.COLUMN_AMOUNT, debt.getAmount());
        debtContentValues.put(DebtsEntry.COLUMN_TYPE, debt.getDebtType());
        debtContentValues.put(DebtsEntry.COLUMN_STATUS, debt.getStatus());

        db.update(DebtsEntry.TABLE_NAME, debtContentValues, DebtsEntry.COLUMN_ENTRY_ID + " = ?", new String[]{debt.getId()});
    }

    @Override
    public String saveNewPerson(@NonNull Person person) {

        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

        String personId = UUID.randomUUID().toString();
        ContentValues personValues = new ContentValues();
        personValues.put(PersonsEntry.COLUMN_ENTRY_ID, personId);
        personValues.put(PersonsEntry.COLUMN_NAME, person.getFullname());
        personValues.put(PersonsEntry.COLUMN_PHONE_NO, person.getPhoneNumber());
        db.insert(PersonsEntry.TABLE_NAME, null, personValues);

        return personId;
    }

    @Override
    public Person getPerson(@NonNull String personId) {
        checkNotNull(personId);

        SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + PersonsEntry.TABLE_NAME + " WHERE " +
                PersonsEntry.COLUMN_ENTRY_ID + " = ?", new String[] {personId});

        Person person = null;
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            String entryId = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_ENTRY_ID));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_NAME));
            String phoneNo = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_PHONE_NO));

            person = new Person(entryId, fullName, phoneNo);
        }

        if(cursor != null) {
            cursor.close();
        }

        db.close();
        return person;
    }

    private String getPersonIdIfAlreadyExist(String phoneNumber) {

        if (!StringUtil.isEmpty(phoneNumber)) {
            SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT " + PersonsEntry.COLUMN_ENTRY_ID + " FROM " + PersonsEntry.TABLE_NAME +
                    " WHERE " + PersonsEntry.COLUMN_PHONE_NO + " = ?", new String[]{String.valueOf(phoneNumber)});
            String id = "";
            if (cursor.moveToFirst()) {
                id = cursor.getString(cursor.getColumnIndexOrThrow(PersonsEntry.COLUMN_ENTRY_ID));
            }
            cursor.close();
            return id;
        }
        return "";
    }

    private boolean personAlreadyExist(String phoneNumber) {

        if (!StringUtil.isEmpty(phoneNumber)) {
            SQLiteDatabase db = mDebtsDbHelper.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT " + PersonsEntry.COLUMN_PHONE_NO + " FROM " + PersonsEntry.TABLE_NAME +
                    " WHERE " + PersonsEntry.COLUMN_PHONE_NO  + " = ?", new String[]{String.valueOf(phoneNumber)});
            if (cursor.moveToFirst()) {
                return true;
            }
            cursor.close();
        }
        return false;
    }
}