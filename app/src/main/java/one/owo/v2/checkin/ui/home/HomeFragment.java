package one.owo.v2.checkin.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.Collator;
import android.icu.text.Transliterator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import one.owo.v2.checkin.R;
import one.owo.v2.checkin.databinding.FragmentHomeBinding;
import one.owo.v2.checkin.databinding.FragmentImportFileBinding;
import one.owo.v2.checkin.sercices.Person;
public class HomeFragment extends Fragment {

    private FragmentHomeBinding homeBinding;
    private FragmentImportFileBinding importFileBinding;
    private List<Person> personList = new ArrayList<>();
    private ArrayList<Object> titleRowItem = new ArrayList<>();
    private int currentIndex = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View root;

        // 从 SharedPreferences 加载数据
        personList = loadPersonListFromPreferences();
        titleRowItem = loadTitleRowFromPreferences();


        //判断personList为不为空
        if (!personList.isEmpty()) {
            homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
            Bundle bundle = new Bundle();
            bundle.putSerializable("personList", new ArrayList<>(personList));
            bundle.putSerializable("titleRowItem", titleRowItem);

            loadHome();
            showGreetingText();

            homeBinding.startCheckIn.setOnClickListener(v -> {
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.action_homeFragment_to_checkInFragment, bundle);
            });

            root = homeBinding.getRoot();
            updateUI();
        } else {
            importFileBinding = FragmentImportFileBinding.inflate(inflater, container, false);

            importFileBinding.fileChooserButton.setOnClickListener(v -> requestPermissionAndOpenFileChooser());

            root = importFileBinding.getRoot();
            updateUI();
        }
        return root;
    }

    private void loadHome() {
        homeBinding.textClock.setFormat24Hour("HH:mm:ss");
        homeBinding.textClock.setFormat12Hour("HH:mm:ss");
    }

    private void showGreetingText() {
        final TextView welcome = homeBinding.welcome;
        int hour = ZonedDateTime.now().getHour();
        String greeting;
        if (hour >= 6 && hour < 12) {
            greeting = "早上好！";
        } else if (hour >= 12 && hour < 18) {
            greeting = "下午好！";
        } else if (hour >= 18 && hour < 22) {
            greeting = "晚上好！";
        } else {
            greeting = "欢迎！";
        }
        welcome.setText(greeting);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeBinding = null;
    }
    private void requestPermissionAndOpenFileChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                startActivity(intent);
            } else {
                openFileChooser();
                updateUI();
            }
        } else {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                openFileChooser();
                updateUI();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意权限，执行操作
                openFileChooser();
            } else {
                // 用户拒绝权限
                Toast.makeText(getContext(), "读取外部存储权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fileChooserLauncher.launch(Intent.createChooser(intent, "选择 Excel 文件"));
    }


    @SuppressLint("SetTextI18n")
    private void updateUI() {
    }


    // 注册文件选择器
    private ActivityResultLauncher<Intent> fileChooserLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        personList = readExcelData(uri);
                        if (!personList.isEmpty()) {
                            savePersonListToPreferences(personList);
                            currentIndex = 0;
                            updateUI();
                        }
                    }
                }
            }
    );

    private List<Person> readExcelData(Uri uri) {
        List<Person> list = new ArrayList<>();
        try (InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                Toast.makeText(getActivity(), "无法打开文件", Toast.LENGTH_SHORT).show();
                return list;
            }

            // 使用 Apache POI 解析 Excel
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            //定义一个arraylist用于存储标题行的数据
            ArrayList<String> titleRowItem = new ArrayList<>();

            // 定义列索引
            int nameColumnIndex = -1;
            int idColumnIndex = -1;

            // 读取标题行（假定第一行为标题行）
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                Toast.makeText(getActivity(), "文件格式错误：标题行为空", Toast.LENGTH_SHORT).show();
                return list;
            }

            // 严格匹配列名
            for (int i = 0; i < titleRow.getPhysicalNumberOfCells(); i++) {
                Cell cell = titleRow.getCell(i);
                if (cell != null) {
                    String columnHeader = cell.getStringCellValue().trim(); // 去掉多余空格
                    if ("姓名".equals(columnHeader)) {
                        nameColumnIndex = i;
                    } else if ("学号".equals(columnHeader)) {
                        idColumnIndex = i;
                    }
                    titleRowItem.add(columnHeader);
                }
            }

            if (!titleRowItem.isEmpty()) {
                saveTitleRowToPreferences(titleRowItem);
            }

            // 检查是否找到所有所需的列
            if (nameColumnIndex == -1 || idColumnIndex == -1) {
                Toast.makeText(getActivity(), "文件格式错误：未找到所需的列（姓名/学号/寝室号）", Toast.LENGTH_SHORT).show();
                return list;
            }

            // 读取数据行
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    String name = row.getCell(nameColumnIndex) != null ? row.getCell(nameColumnIndex).toString().trim() : "";
                    String id = row.getCell(idColumnIndex) != null ? row.getCell(idColumnIndex).toString().trim() : "";
                    ArrayList<String> allInfo = new ArrayList<>();
                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        allInfo.add(row.getCell(i) != null ? row.getCell(i).toString().trim() : "#");
                    }
                    // 创建人员对象并添加到列表
                    list.add(new Person(name, id, allInfo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "读取文件失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return list;
    }

    private void savePersonListToPreferences(List<Person> personList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AttendanceApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Collator collator = Collator.getInstance();
        Transliterator pinyinConverter = Transliterator.getInstance("Han-Latin; NFD; [:Nonspacing Mark:] Remove; NFC");
        personList.sort((p1, p2) -> {
            String pinyin1 = pinyinConverter.transliterate(p1.getName());
            String pinyin2 = pinyinConverter.transliterate(p2.getName());
            return collator.compare(pinyin1, pinyin2);
        });
        Gson gson = new Gson();
        String json = gson.toJson(personList);
        editor.putString("personList", json);
        editor.apply();
    }

    private void saveTitleRowToPreferences(ArrayList<String> titleRowItem) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AttendanceApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(titleRowItem);
        editor.putString("titleRow", json);
        editor.apply();
    }

    private List<Person> loadPersonListFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AttendanceApp", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("personList", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Person>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    private ArrayList<Object> loadTitleRowFromPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AttendanceApp", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("titleRow", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }
}
