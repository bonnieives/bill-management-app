package com.example.bill_management_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class AddBillActivity extends AppCompatActivity {

    LinearLayout navIcons;
    LinearLayout clientDetails;
    ImageButton btnHome, btnProfile;
    Button buttonSave, buttonCancel;
    TextView textViewFirstName, textViewAvailableCreditNumeric;
    EditText editTextAccountNumber, editTextAmount, editTextDueDate;

    Spinner spinnerBillerName;

    FirebaseAuth fbaseAuth;

    FirebaseDatabase fbaseDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill_page);

        editTextAccountNumber = findViewById(R.id.editTextAccountNumber);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDueDate = findViewById(R.id.editTextDueDate);

        Intent intent = getIntent();
        Client oneClient = (Client) intent.getSerializableExtra("oneClient");

        // HEADER ICONS FUNCTIONALITY
        navIcons = findViewById(R.id.includeTopIcons);
        btnProfile = navIcons.findViewById(R.id.btnProfile);
        btnHome = navIcons.findViewById(R.id.btnHome);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddBillActivity.this, ClientProfilePageActivity.class);
                intent.putExtra("oneClient", oneClient);
                startActivity(intent);
                finish();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddBillActivity.this, ClientDashboard.class);
                intent.putExtra("oneClient", oneClient);
                startActivity(intent);
                finish();
            }
        });

        // PROFILE DETAILS
        clientDetails = findViewById(R.id.includeCustomerHeader);
        textViewFirstName = findViewById(R.id.textViewFirstName);
        textViewAvailableCreditNumeric = findViewById(R.id.textViewAvailableCreditNumeric);

        textViewFirstName.setText("Hello, " + oneClient.getFirstName());
        textViewAvailableCreditNumeric.setText(String.valueOf(oneClient.getCredit()));

        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        spinnerBillerName = findViewById(R.id.spinnerBillerName);
        // generate spinner
        fbaseDB = FirebaseDatabase.getInstance();
        DatabaseReference billers = fbaseDB.getReference("billers");

        billers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Biller> listOfBillers = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String billerName = childSnapshot.child("billerName").getValue(String.class);
                    String billerId = childSnapshot.child("billerID").getValue(String.class);

                    // Add billerName to the list
                    if (billerName != null && billerId != null) {
                        Biller oneBiller = new Biller();
                        oneBiller.setBillerID(billerId);
                        oneBiller.setBillerName(billerName);
                        listOfBillers.add(oneBiller);
                    }
                }

                CustomDropdownBillersAdapter adapterDropdown = new CustomDropdownBillersAdapter(getApplicationContext(),listOfBillers);
                spinnerBillerName.setAdapter(adapterDropdown);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                System.err.println("Error fetching data: " + databaseError);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editTextAccountNumber = findViewById(R.id.editTextAccountNumber);
                editTextAmount = findViewById(R.id.editTextAmount);
                editTextDueDate = findViewById(R.id.editTextDueDate);

                ArrayList<EditText> listRequiredFields = new ArrayList<>();
                listRequiredFields.add(editTextAccountNumber);
                listRequiredFields.add(editTextAmount);
                listRequiredFields.add(editTextDueDate);

                // Validation block for Required Fields
                for (EditText field: listRequiredFields) {
                    if (field.getText().toString().trim().isEmpty()) {
                        field.setError("This field is required.");
                        field.requestFocus();
                        return;
                    }
                }

                Bill newBill = new Bill();
                Biller oneBiller = (Biller)spinnerBillerName.getSelectedItem();

                newBill.setBillerID(oneBiller.getBillerID());
                newBill.setAccountNumber(Integer.valueOf(editTextAccountNumber.getText().toString().trim()));
                newBill.setAmount(Double.valueOf(editTextAmount.getText().toString().trim()));
                newBill.setDateDue(new Date(editTextDueDate.getText().toString()));
                newBill.setStatus(EnumPaymentStatus.Unpaid);

                generateUniqueID(newBill,oneClient,oneBiller);
            }
        });
    }

    private int generateRandomID() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    private void handleGeneratedID(Client oneClient, Bill newBill, Biller oneBiller) {

        fbaseDB = FirebaseDatabase.getInstance();
        DatabaseReference clients = fbaseDB.getReference("clients").child(oneClient.getUserID());
        DatabaseReference listOfBills = clients.child("listOfBills");

        // update DB
        listOfBills.child(String.valueOf(newBill.getBillID())).setValue(true);
    }

    private void generateUniqueID(Bill newBill, Client oneClient, Biller oneBiller) {

        fbaseDB = FirebaseDatabase.getInstance();
        DatabaseReference bills = fbaseDB.getReference("bills");

        bills.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int generatedID = generateRandomID();

                // check if generated ID exists
                if (!snapshot.hasChild(String.valueOf(generatedID))) {

                    newBill.setBillID(generatedID);
                    newBill.setBillerID(oneBiller.getBillerID());

                    // create the entry in bills table
                    bills.child(String.valueOf(generatedID)).child("billID").setValue(String.valueOf(newBill.getBillID()));
                    bills.child(String.valueOf(generatedID)).child("billerID").setValue(newBill.getBillerID());
                    bills.child(String.valueOf(generatedID)).child("accountNumber").setValue(newBill.getAccountNumber());
                    bills.child(String.valueOf(generatedID)).child("dateDue").setValue(newBill.getDateDue());
                    bills.child(String.valueOf(generatedID)).child("amount").setValue(newBill.getAmount());
                    bills.child(String.valueOf(generatedID)).child("status").setValue(newBill.getStatus());

                    handleGeneratedID(oneClient, newBill,oneBiller);

                } else {
                    generateUniqueID(newBill,oneClient,oneBiller);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}