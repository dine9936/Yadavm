package com.example.yadavm.Dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yadavm.Activity.MainActivity;
import com.example.yadavm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class OtpLoginSheet extends BottomSheetDialogFragment {
    private String phone,name,password,address , verificationCodeBySystem;
    private TextView textViewPhoneOtp;
    private Button buttonVerify;
    private EditText editTextOtp;

    DatabaseReference reference;
    DialogLoading loading;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.otp_bottom_fragment,container,false);
        getDialog().setCanceledOnTouchOutside(false);
        loading = new DialogLoading();

        phone = getArguments().getString("phone");

        reference = FirebaseDatabase.getInstance().getReference();
        editTextOtp = view.findViewById(R.id.verification_code);

        textViewPhoneOtp = view.findViewById(R.id.phone_number_otp);
        textViewPhoneOtp.setText("+91"+phone);

        buttonVerify = view.findViewById(R.id.button_verify);
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonVerify.setEnabled(false);
                String code = editTextOtp.getText().toString().trim();

                if (code.isEmpty()){
                    Toast.makeText(getContext(), "Enter verification code", Toast.LENGTH_SHORT).show();
                    buttonVerify.setEnabled(true);
                }
                else {
                    loading.show(getChildFragmentManager(),"Loading");
                    verifyCode(code);
                    buttonVerify.setEnabled(true);
                }
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sendVerificationCodeToUser(phone);
    }

    private void sendVerificationCodeToUser(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,   // Activity (for callback binding)
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        editTextOtp.setText(code);
                        //progressBar.setVisibility(View.VISIBLE);
                        loading.show(getChildFragmentManager(),"Loading");
                        verifyCode(code);

                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);


                    verificationCodeBySystem = s;
                }
            };

    private void verifyCode(String codeByUser) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);

    }
    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().finish();

                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
