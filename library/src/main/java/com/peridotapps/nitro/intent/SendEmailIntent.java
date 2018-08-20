package com.peridotapps.nitro.intent;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.peridotapps.nitro.R;

import java.util.ArrayList;
import java.util.List;

public class SendEmailIntent {

    private SendEmailIntent() {
    }

    public static class Builder extends IntentBuilder {

        private Intent emailIntent;

        private List<String> recipients;
        private List<String> carbonCopyRecipients;
        private List<String> blindCarbonCopyRecipients;
        private String subject;
        private String body;
        private String format;
        private String intentPickerCaption;
        private boolean useIntentChooser;

        public Builder() {
            emailIntent = null;
            recipients = new ArrayList<>();
            carbonCopyRecipients = new ArrayList<>();
            blindCarbonCopyRecipients = new ArrayList<>();
            subject = "";
            body = "";
            format = getString(R.string.email_format);
            intentPickerCaption = getString(R.string.send_email_launcher_caption);
            useIntentChooser = true;
        }

        @NonNull
        public Builder addRecipient(@NonNull String emailAddress) {
            String recipient = formatEmailAddress(emailAddress);

            if (!recipientAdded(recipient)) {
                recipients.add(recipient);
            }

            return this;
        }

        @NonNull
        public Builder addCarbonCopyRecipient(@NonNull String emailAddress) {
            emailAddress = formatEmailAddress(emailAddress);

            if (!recipientAdded(emailAddress)) {
                carbonCopyRecipients.add(emailAddress);
            }

            return this;
        }

        @NonNull
        public Builder addBlindCarbonCopyRecipient(@NonNull String emailAddress) {
            emailAddress = formatEmailAddress(emailAddress);

            if (!recipientAdded(emailAddress)) {
                blindCarbonCopyRecipients.add(emailAddress);
            }

            return this;
        }

        @NonNull
        public Builder addRecipient(@StringRes int emailAddressStringResourceId) {
            return addRecipient(getString(emailAddressStringResourceId));
        }

        @NonNull
        public Builder addCarbonCopyRecipient(@StringRes int emailAddressStringResourceId) {
            return addCarbonCopyRecipient(getString(emailAddressStringResourceId));
        }

        @NonNull
        public Builder addBlindCarbonCopyRecipient(@StringRes int emailAddressStringResourceId) {
            return addBlindCarbonCopyRecipient(getString(emailAddressStringResourceId));
        }

        @NonNull
        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        @NonNull
        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        @NonNull
        public Builder setFormat(String format) {
            this.format = format;
            return this;
        }

        @NonNull
        public Builder setIntentPickerCaption(String intentPickerCaption) {
            this.intentPickerCaption = intentPickerCaption;
            return this;
        }

        @NonNull
        public Builder setSubject(@StringRes int subjectStringResId) {
            this.subject = getString(subjectStringResId);
            return this;
        }

        @NonNull
        public Builder setBody(@StringRes int bodyStringResId) {
            this.body = getString(bodyStringResId);
            return this;
        }

        @NonNull
        public Builder setFormat(@StringRes int formatStringResId) {
            this.format = getString(formatStringResId);
            return this;
        }

        @NonNull
        public Builder setIntentPickerCaption(@StringRes int intentPickerCaptionStringResId) {
            this.intentPickerCaption = getString(intentPickerCaptionStringResId);
            return this;
        }

        @NonNull
        public Builder setUseIntentChooser(boolean useIntentChooser) {
            this.useIntentChooser = useIntentChooser;
            return this;
        }

        @Override
        @NonNull
        public Intent build(@Nullable Integer flags) {
            if (emailIntent == null) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND).setType(format)
                        .putExtra(Intent.EXTRA_EMAIL, getRecipientArray(recipients))
                        .putExtra(Intent.EXTRA_CC, getRecipientArray(carbonCopyRecipients))
                        .putExtra(Intent.EXTRA_BCC, getRecipientArray(blindCarbonCopyRecipients))
                        .putExtra(Intent.EXTRA_SUBJECT, processNull(subject))
                        .putExtra(Intent.EXTRA_TEXT, processNull(body));

                if (flags != null) {
                    emailIntent.addFlags(flags);
                }

                if (useIntentChooser) {
                    emailIntent = Intent.createChooser(sendIntent, processNull(intentPickerCaption, getString(R.string.send_email_launcher_caption)));
                } else {
                    emailIntent = sendIntent;
                }
            }

            return emailIntent;
        }

        @NonNull
        private String[] getRecipientArray(@NonNull List<String> recipientList) {
            return (recipientList.size() > 0) ? recipientList.toArray(new String[]{}) : new String[]{};
        }

        private boolean recipientAdded(@NonNull String emailAddress) {
            return recipients.contains(emailAddress) || carbonCopyRecipients.contains(emailAddress) || blindCarbonCopyRecipients.contains(emailAddress) || TextUtils.isEmpty(emailAddress);
        }

        @NonNull
        private String formatEmailAddress(@NonNull String emailAddress) {
            if (emailAddress.contains("mailto:")) {
                emailAddress = emailAddress.replace("mailto:", "");
            }
            return emailAddress.trim()
                    .toLowerCase();
        }

    }
}
