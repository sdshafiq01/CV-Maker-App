package in.thbd.cvmaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 101;
    EditText et_name, et_email, et_phone, et_objects, et_skills, et_declarations;
    ArrayList<String> educationList = new ArrayList<>();
    ArrayList<String> experienceList = new ArrayList<>();
    ArrayList<String> projectsList = new ArrayList<>();

    LinearLayout educationLayout, experienceLayout, projectsLayout;
    Button btn_add_education, btn_add_experience, btn_add_projects, btn_save_cv, btn_generate_cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkPermissions();

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_objects = findViewById(R.id.et_objects);
        et_skills = findViewById(R.id.et_skills);
        et_declarations = findViewById(R.id.et_declarations);

        educationLayout = findViewById(R.id.educationLayout);
        experienceLayout = findViewById(R.id.experienceLayout);
        projectsLayout = findViewById(R.id.projectsLayout);

        btn_add_education = findViewById(R.id.btn_add_education);
        btn_add_experience = findViewById(R.id.btn_add_experience);
        btn_add_projects = findViewById(R.id.btn_add_projects);
        btn_save_cv = findViewById(R.id.btn_save_cv);
        btn_generate_cv = findViewById(R.id.btn_generate_cv);


        btn_add_education.setOnClickListener(view -> {
            addEducationField();
        });
        btn_add_experience.setOnClickListener(view -> {
            addExperienceField();
        });

        btn_add_projects.setOnClickListener(view -> {
            addProjectField();
        });

        btn_save_cv.setOnClickListener(view -> {
            boolean isSaved = saveCVData();
            if (isSaved) {
                Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show();
                btn_save_cv.setVisibility(View.GONE);
                btn_generate_cv.setVisibility(View.VISIBLE);
            } else Toast.makeText(this, "Something wrong!", Toast.LENGTH_SHORT).show();

        });

        btn_generate_cv.setOnClickListener(view -> {
            boolean isSuccess = checkPermissions();
            if (isSuccess) generateCV();
            else
                Toast.makeText(this, "Need storage permission for save data", Toast.LENGTH_SHORT).show();
        });

    }

    private void generateCV() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                595,
                842,
                1
        ).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // create text style & shape
        Paint paint = new Paint();
        paint.setTextSize(14);

        //  Heading style
        Paint boldpaint = new Paint();
        boldpaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        boldpaint.setTextSize(20);

        int leftMargin = 50;
        int topMargin = 70;
        int pageWidth = pageInfo.getPageWidth() - (2 * leftMargin);


//        set starting point text margin
        int x = leftMargin;
        int y = topMargin;

//        Add name into content
        String heading = et_name.getText().toString().trim();
        page.getCanvas().drawText(heading, x, y, boldpaint);
        y += 20; // y increasing for add space into down

//        change heading size for another
        boldpaint.setTextSize(18);

//        Add email
        page.getCanvas().drawText("Email: " + et_email.getText().toString().trim(), x, y, paint);
        y += 20;

//        Add Phone number
        page.getCanvas().drawText("Phone: " + et_phone.getText().toString().trim(), x, y, paint);
        y += 50;

        //        Add Objects heading into content
        heading = "Objects";
        page.getCanvas().drawText(heading, x, y, boldpaint);
        y += 25;

//        Add Objects content
        page.getCanvas().drawText(et_objects.getText().toString().trim(), x, y, paint);
        y += 50;


//        Add Education heading into content
        heading = "Education";
        page.getCanvas().drawText(heading, x, y, boldpaint);
        y += 25;

//        Add Education content
        for (String items : educationList) {
            page.getCanvas().drawText(">> " + items, x, y, paint);
            y += 20;
        }
        y += 30;


//        Add Experience heading into content
        heading = "Experience";
        page.getCanvas().drawText(heading, x, y, boldpaint);
        y += 25;

//        Add Education content
        for (String items : experienceList) {
            page.getCanvas().drawText(">> " + items, x, y, paint);
            y += 20;
        }
        y += 30;

        //        Add Skills heading into content
        heading = "Skills";
        page.getCanvas().drawText(heading, x, y, boldpaint);
        y += 25;

//        Add Objects content
        page.getCanvas().drawText(et_skills.getText().toString().trim(), x, y, paint);
        y += 50;


        //        Add Experience heading into content
        heading = "Projects";
        page.getCanvas().drawText(heading, x, y, boldpaint);
        y += 25;

//        Add Education content
        for (String items : projectsList) {
            page.getCanvas().drawText(">> " + items, x, y, paint);
            y += 20;
        }
        y += 30;


//        Add Declarations heading into content
        heading = "Declarations";
        page.getCanvas().drawText(heading, x, y, boldpaint);
        y += 25;

//        Add Objects content
        y = drawTextWithWrapping(page.getCanvas(), et_declarations.getText().toString().trim(), x, y, pageWidth, paint);

        pdfDocument.finishPage(page);

        File downloadDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS),"CV MAKER"
        );
        try {
            if (!downloadDir.exists()) downloadDir.mkdirs();

            File file = new File(downloadDir, et_name.getText().toString().trim()+" my-cv.pdf");
            if (file.exists()) file.delete();

            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Hello, Your CV is ready into Downloads/CV Maker folder", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Toast.makeText(this, "Ops! Something wrong!", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

        pdfDocument.close();
//        lets run!

    }

    private int drawTextWithWrapping(Canvas canvas, String txt, int x, int y, int maxWidth, Paint paint) {

        int lineHeight = (int) (paint.descent() - paint.ascent());
        int start = 0, end;
        while (start < txt.length()) {
            end = paint.breakText(txt, start, txt.length(), true, maxWidth, null);
            canvas.drawText(txt, start, start + end, x, y, paint);
            start += end;
            y += lineHeight;
        }
        return y;

    }


    private boolean saveCVData() {
        educationList.clear();
        experienceList.clear();
        projectsList.clear();

        for (int i = 0; i < educationLayout.getChildCount(); i++) {
            EditText editText = (EditText) educationLayout.getChildAt(i);
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Can't be empty!");
                return false;
            }
            educationList.add(editText.getText().toString().trim());

        }

        for (int i = 0; i < experienceLayout.getChildCount(); i++) {
            EditText editText = (EditText) experienceLayout.getChildAt(i);
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Can't be empty!");
                return false;
            }
            experienceList.add(editText.getText().toString().trim());

        }

        for (int i = 0; i < projectsLayout.getChildCount(); i++) {
            EditText editText = (EditText) projectsLayout.getChildAt(i);
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Can't be empty!");
                return false;
            }
            projectsList.add(editText.getText().toString().trim());

        }


        return true;
    }


    private void addEducationField() {
        EditText etEdu = (EditText) new EditText(this);
        etEdu.setHint("Enter your education");
        educationLayout.addView(etEdu);

    }

    private void addExperienceField() {
        EditText etEdu = (EditText) new EditText(this);
        etEdu.setHint("Enter your experience");
        experienceLayout.addView(etEdu);


    }

    private void addProjectField() {
        EditText etEdu = (EditText) new EditText(this);
        etEdu.setHint("Enter your projects");
        projectsLayout.addView(etEdu);
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {

                // Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                // Permission already granted
                return true;

            }
        } else {
            // For older versions below Marshmallow, permission is granted during installation
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                // if granter call to generate pdf
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}