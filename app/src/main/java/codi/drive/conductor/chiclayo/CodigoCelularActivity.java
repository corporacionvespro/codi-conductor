package codi.drive.conductor.chiclayo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CodigoCelularActivity extends AppCompatActivity {
    @BindView(R.id.etCodigo) EditText etCodigo;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_celular);
        ButterKnife.bind(this);
        verificar(Registro1Activity.celular);
        etCodigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etCodigo.getText().length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, etCodigo.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void verificar(String numero) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+51" + numero)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            Log.d("onVerificationCompleted", "" + credential);
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            e.printStackTrace();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Log.e("onVerificationFailed", "invalid request");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Log.e("onVerificationFailed", "The SMS quota for the project has been exceeded");
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d("onCodeSent", verificationId);
            id = verificationId;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("signInWithPhoneAuthCred", "signInWithCredential:success");
                FirebaseUser user = task.getResult().getUser();
                Registro1Activity.btnVerificar.setVisibility(View.GONE);
                Registro1Activity.btnSiguiente.setVisibility(View.VISIBLE);
                finish();
            } else {
                Log.w("onComplete", "signInWithCredential:failure", task.getException());
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Código ingresado no es válido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}