package com.example.thepizzamaniaproject.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thepizzamaniaproject.Domain.Branch;
import com.example.thepizzamaniaproject.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BranchManagementActivity extends AppCompatActivity {

    private RecyclerView branchesRecyclerView;
    private BranchAdapter branchAdapter;
    private List<Branch> branchList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private static final int LOCATION_PICKER_REQUEST = 1001;

    // Variables to store selected location data
    private double selectedLatitude = 0;
    private double selectedLongitude = 0;
    private String selectedAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_management);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("branches");

        initializeUI();
        setupRecyclerView();
        loadBranchesFromFirebase();
    }

    private void initializeUI() {
        branchesRecyclerView = findViewById(R.id.branchesRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        FloatingActionButton fabAddBranch = findViewById(R.id.fabAddBranch);
        fabAddBranch.setOnClickListener(v -> showAddEditBranchDialog(null));
    }

    private void setupRecyclerView() {
        branchesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        branchAdapter = new BranchAdapter(branchList);
        branchesRecyclerView.setAdapter(branchAdapter);
    }

    private void loadBranchesFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                branchList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Branch branch = snapshot.getValue(Branch.class);
                    if (branch != null) {
                        branch.setId(snapshot.getKey());
                        branchList.add(branch);
                    }
                }
                branchAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BranchManagementActivity.this, "Failed to load branches: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditBranchDialog(Branch branch) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(branch == null ? "Add New Branch" : "Edit Branch");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_branch, null);
        builder.setView(dialogView);

        TextInputEditText etBranchName = dialogView.findViewById(R.id.etBranchName);
        TextView tvSelectedAddress = dialogView.findViewById(R.id.tvSelectedAddress);
        TextInputEditText etBranchPhone = dialogView.findViewById(R.id.etBranchPhone);
        TextInputEditText etManagerName = dialogView.findViewById(R.id.etManagerName);

        // Initialize location data
        selectedLatitude = 0;
        selectedLongitude = 0;
        selectedAddress = "";

        if (branch != null) {
            etBranchName.setText(branch.getName());
            etBranchPhone.setText(branch.getPhone());
            etManagerName.setText(branch.getManager());

            if (branch.getAddress() != null && !branch.getAddress().isEmpty()) {
                tvSelectedAddress.setText(branch.getAddress());
                selectedAddress = branch.getAddress();
                selectedLatitude = branch.getLatitude();
                selectedLongitude = branch.getLongitude();
            }
        }

        AlertDialog dialog = builder.create();

        // Handle location picking
        dialogView.findViewById(R.id.btnPickLocation).setOnClickListener(v -> {
            Intent intent = new Intent(BranchManagementActivity.this, LocationPickerActivity.class);
            startActivityForResult(intent, LOCATION_PICKER_REQUEST);
            dialog.dismiss(); // Dismiss the dialog while picking location
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = etBranchName.getText().toString().trim();
            String phone = etBranchPhone.getText().toString().trim();
            String manager = etManagerName.getText().toString().trim();

            if (validateInput(name, selectedAddress, phone, manager)) {
                if (branch == null) {
                    // Add new branch to Firebase
                    Branch newBranch = new Branch(name, selectedAddress, selectedLatitude, selectedLongitude, phone, manager);
                    String key = databaseReference.push().getKey();
                    if (key != null) {
                        databaseReference.child(key).setValue(newBranch)
                                .addOnSuccessListener(aVoid -> Toast.makeText(BranchManagementActivity.this, "Branch added successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(BranchManagementActivity.this, "Failed to add branch: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // Update existing branch in Firebase
                    branch.setName(name);
                    branch.setAddress(selectedAddress);
                    branch.setLatitude(selectedLatitude);
                    branch.setLongitude(selectedLongitude);
                    branch.setPhone(phone);
                    branch.setManager(manager);

                    databaseReference.child(branch.getId()).setValue(branch)
                            .addOnSuccessListener(aVoid -> Toast.makeText(BranchManagementActivity.this, "Branch updated successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(BranchManagementActivity.this, "Failed to update branch: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Handle location picker result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_PICKER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                selectedLatitude = data.getDoubleExtra("latitude", 0);
                selectedLongitude = data.getDoubleExtra("longitude", 0);
                selectedAddress = data.getStringExtra("address");

                // Reopen the dialog with the selected location
                showAddEditBranchDialog(null);
            }
        }
    }

    private boolean validateInput(String name, String address, String phone, String manager) {
        if (name.isEmpty()) {
            showError("Please enter branch name");
            return false;
        }
        if (address.isEmpty()) {
            showError("Please select a location");
            return false;
        }
        if (phone.isEmpty()) {
            showError("Please enter phone number");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // RecyclerView Adapter
    private class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

        private List<Branch> branches;

        public BranchAdapter(List<Branch> branches) {
            this.branches = branches;
        }

        @NonNull
        @Override
        public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_branch, parent, false);
            return new BranchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
            Branch branch = branches.get(position);
            holder.tvBranchName.setText(branch.getName());
            holder.tvBranchAddress.setText(branch.getAddress());
            holder.tvBranchPhone.setText(branch.getPhone());
            holder.tvManagerName.setText(branch.getManager());

            holder.btnEdit.setOnClickListener(v -> showAddEditBranchDialog(branch));
            holder.btnDelete.setOnClickListener(v -> deleteBranch(branch));
        }

        @Override
        public int getItemCount() {
            return branches.size();
        }

        private void deleteBranch(Branch branch) {
            new MaterialAlertDialogBuilder(BranchManagementActivity.this)
                    .setTitle("Delete Branch")
                    .setMessage("Are you sure you want to delete " + branch.getName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        databaseReference.child(branch.getId()).removeValue()
                                .addOnSuccessListener(aVoid -> Toast.makeText(BranchManagementActivity.this, "Branch deleted successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(BranchManagementActivity.this, "Failed to delete branch: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        class BranchViewHolder extends RecyclerView.ViewHolder {
            TextView tvBranchName, tvBranchAddress, tvBranchPhone, tvManagerName;
            com.google.android.material.button.MaterialButton btnEdit, btnDelete;

            public BranchViewHolder(@NonNull View itemView) {
                super(itemView);
                tvBranchName = itemView.findViewById(R.id.tvBranchName);
                tvBranchAddress = itemView.findViewById(R.id.tvBranchAddress);
                tvBranchPhone = itemView.findViewById(R.id.tvBranchPhone);
                tvManagerName = itemView.findViewById(R.id.tvManagerName);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}