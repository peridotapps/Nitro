package com.peridotapps.nitro.intent;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.peridotapps.nitro.R;

public class OpenWebUrlIntent {

    private OpenWebUrlIntent() {
    }

    public static class Builder extends IntentBuilder {

        private static final String PREFIX_HTTP = "http://";
        private static final String PREFIX_HTTPS = "https://";

        private Intent webLinkIntent;

        private boolean useChooser;
        private String chooserCaption;
        private String url;

        public Builder() {
            webLinkIntent = null;
            url = null;
            useChooser = true;
            chooserCaption = getString(R.string.link_launch_caption);
        }

        @NonNull
        public Builder setUrl(@NonNull String url) {
            this.url = formatUrl(url);
            return this;
        }

        @NonNull
        public Builder setChooserCaption(@NonNull String chooserCaption) {
            this.chooserCaption = chooserCaption;
            return this;
        }

        @NonNull
        public Builder setUrl(@StringRes int urlStringResId) {
            this.url = getString(urlStringResId);
            return this;
        }

        @NonNull
        public Builder setChooserCaption(@StringRes int chooserCaptionStringResId) {
            this.chooserCaption = getString(chooserCaptionStringResId);
            return this;
        }

        @NonNull
        public Builder setUseChooser(boolean useChooser) {
            this.useChooser = useChooser;
            return this;
        }

        @Override
        @NonNull
        public Intent build(@Nullable Integer flags) {
            if (webLinkIntent == null) {
                Intent launchUrlIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));

                if (flags != null) {
                    launchUrlIntent.addFlags(flags);
                }

                if (useChooser) {
                    webLinkIntent = Intent.createChooser(launchUrlIntent, chooserCaption);
                } else {
                    webLinkIntent = launchUrlIntent;
                }
            }

            return webLinkIntent;
        }

        private boolean hasWebPrefix(@NonNull String url) {
            return url.startsWith(PREFIX_HTTP)
                    || url.startsWith(PREFIX_HTTPS);
        }


        @NonNull
        private String formatUrl(@NonNull String url) {
            return (hasWebPrefix(url.trim())) ? url.trim() : appendWebPrefix(url);
        }

        @NonNull
        private String appendWebPrefix(@NonNull String url) {
            return String.format("http://%s", url).trim();
        }

    }

}
