package com.mocomp.developer.medicbooks.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.mocomp.developer.medicbooks.R;
import com.mocomp.developer.medicbooks.data.constant.AppConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class AppUtilities {

    private static long backPressed = 0;

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void tapPromptToExit(Activity activity) {
        if (backPressed + 2500 > System.currentTimeMillis()) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(startMain);
            //activity.finish();
        } else {
            showToast(activity.getApplicationContext(), activity.getResources().getString(R.string.tapAgain));
        }
        backPressed = System.currentTimeMillis();
    }

    public static void youtubeLink(Activity activity) {
        updateLink(activity, activity.getString(R.string.youtube_url));
    }

    public static void faceBookLink(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"msir100@yahoo.com"});//write the to email her
        //intent.putExtra(Intent.EXTRA_TEXT,message);
        //intent.putExtra(Intent.EXTRA_SUBJECT, "technical team apply form");//the subject of the email
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    public static void twitterLink(Activity activity) {
        try {
            ApplicationInfo applicationInfo = activity.getPackageManager().getApplicationInfo("com.twitter.android", 0);
            if (applicationInfo.enabled) {
                updateLink(activity, activity.getString(R.string.twitter_user_id));
                return;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        updateLink(activity, activity.getString(R.string.twitter_url));
    }

    public static void WhatsappLink(Activity activity) {
        String url = "https://api.whatsapp.com/send?phone="+"+201126498473";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }

    private static void updateLink(Activity activity, String text) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager packageManager = activity.getPackageManager();
        if (packageManager.resolveActivity(i,
                PackageManager.MATCH_DEFAULT_ONLY) != null) {
            activity.startActivity(i);
        }
    }

    public static void shareApp(Activity activity) {
        try {
            final String appPackageName = activity.getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.share_text) + " https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            activity.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rateThisApp(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(Context mContext, Activity mActivity, Bitmap bitmap) {
        if (PermissionUtilities.isPermissionGranted(mActivity, PermissionUtilities.SD_WRITE_PERMISSIONS, PermissionUtilities.REQUEST_WRITE_STORAGE_DOWNLOAD)) {

            Random rand = new Random();
            int n = 10000;
            n = rand.nextInt(n);
            String fileName = "Wallpaper-" + n;

            FileOutputStream out = null;
            String filePath = getFilename(fileName);
            try {
                out = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                Toast.makeText(mActivity, mContext.getString(R.string.wallpaper_download), Toast.LENGTH_SHORT).show();
                MediaScannerConnection.scanFile(mActivity,
                        new String[]{filePath.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mActivity, mContext.getString(R.string.wallpaper_download_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String getFilename(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), AppConstant.SAVE_TO);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (fileName.contains("/")) {
            fileName = fileName.replace("/", "\\");
        }
        return (file.getAbsolutePath() + "/" + fileName + ".png");
    }


    public static void moreApps(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse(activity.getString(R.string.developer_page)));
            activity.startActivity(intent);
        } catch (Exception e) {
            intent.setData(Uri.parse(activity.getString(R.string.alternate_dev_page)));
            activity.startActivity(intent);
        }

        activity.startActivity(intent);
    }
}
