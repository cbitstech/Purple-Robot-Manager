(define pr-add-namespace (lambda (identifier) (.addNamespace PurpleRobot identifier)))
(define pr-broadcast-intent (lambda (action extras) (.broadcastIntent PurpleRobot action extras)))
(define pr-can-scan-nfc? (lambda () (.canScanNFC PurpleRobot)))
(define pr-cancel-nfc-scan (lambda () (.cancelNFCScan PurpleRobot)))
(define pr-cancel-scheduled-script (lambda (identifier) (.cancelScheduledScript PurpleRobot identifier)))
(define pr-cancel-script-notification (lambda () (.cancelScriptNotification PurpleRobot)))
(define pr-clear-native-dialogs (lambda args (apply .clearNativeDialogs (cons PurpleRobot args))))
(define pr-clear-password (lambda () (.clearPassword PurpleRobot)))
(define pr-clear-triggers (lambda () (.clearTriggers PurpleRobot)))
(define pr-delete-snapshot (lambda (snapshot-id) (.deleteSnapshot PurpleRobot snapshot-id)))
(define pr-delete-trigger (lambda (trigger-id) (.deleteTrigger PurpleRobot trigger-id)))
(define pr-disable-background-image (lambda () (.disableBackgroundImage PurpleRobot)))
(define pr-disable-each-probe (lambda () (.disableEachProbe PurpleRobot)))
(define pr-disable-probe (lambda (probe-name) (.disableProbe PurpleRobot probe-name)))
(define pr-disable-probes (lambda args (.disableProbes PurpleRobot)))
(define pr-disable-trigger (lambda (trigger-id) (.disableTrigger PurpleRobot trigger-id)))
(define pr-emit-reading (lambda args (apply .emitReading (cons PurpleRobot args))))
(define pr-emit-toast (lambda args (apply .emitToast (cons PurpleRobot args))))
(define pr-enable-background-image (lambda () (.enableBackgroundImage PurpleRobot)))
(define pr-enable-probe (lambda (probe-name) (.enableProbe PurpleRobot probe-name)))
(define pr-enable-probes (lambda () (.enableProbes PurpleRobot)))
(define pr-enable-trigger (lambda (trigger-id) (.enableTrigger PurpleRobot trigger-id)))
(define pr-fetch-encrypted-string (lambda args (apply .fetchEncryptedString (cons PurpleRobot args))))
(define pr-fetch-namespace (lambda (namespace) (.fetchNamespace PurpleRobot namespace)))
(define pr-fetch-setting (lambda (key) (.fetchSetting PurpleRobot key)))
(define pr-fetch-string (lambda args (apply .fetchString (cons PurpleRobot args))))
(define pr-fetch-snapshot (lambda (args) (.fetchSnapshot PurpleRobot args)))
(define pr-fetch-snapshot-ids (lambda () (.fetchSnapshotIds PurpleRobot)))
(define pr-fetch-trigger (lambda (trigger-id) (.fetchTrigger PurpleRobot trigger-id)))
(define pr-fetch-trigger-ids (lambda () (.fetchTriggerIds PurpleRobot)))
(define pr-fetch-user-hash (lambda () (.fetchUserHash PurpleRobot)))
(define pr-fetch-user-id (lambda () (.fetchUserId PurpleRobot)))
(define pr-fire-trigger (lambda (trigger-id) (.fireTrigger PurpleRobot trigger-id)))
(define pr-fetch-config (lambda () (.fetchConfig PurpleRobot)))
(define pr-fetch-label (lambda (context name) (.fetchLabel PurpleRobot context name)))
(define pr-fetch-labels (lambda (context name labels) (.fetchLabels PurpleRobot context name labels)))
(define pr-launch-application (lambda args (apply .launchApplication (cons PurpleRobot args))))
(define pr-launch-internal-url (lambda (url) (.launchInternalUrl PurpleRobot url)))
(define pr-launch-url (lambda (url) (.launchUrl PurpleRobot url)))
(define pr-list-tones (lambda () (.listTones PurpleRobot)))
(define pr-log (lambda (message) (.log PurpleRobot message)))
(define pr-namespaces (lambda () (.namespaces PurpleRobot)))
(define pr-package-name (lambda (applicationName) (.packageForApplicationName PurpleRobot applicationName)))
(define pr-persist-encrypted-string (lambda args (apply .persistEncryptedString (cons PurpleRobot args))))
(define pr-persist-string (lambda args (apply .persistString (cons PurpleRobot args))))
(define pr-play-default-tone (lambda args (apply .playDefaultTone (cons PurpleRobot args))))
(define pr-play-tone (lambda args (apply .playTone (cons PurpleRobot args))))
(define pr-probes-enabled? (lambda () (.probesEnabled PurpleRobot)))
(define pr-probe-config (lambda (args) (.probeConfig PurpleRobot args)))
(define pr-read-url (lambda args (apply .readUrl (cons PurpleRobot args))))
(define pr-readings (lambda () (.readings PurpleRobot)))
(define pr-reset-trigger (lambda (trigger-id) (.resetTrigger PurpleRobot trigger-id)))
(define pr-restore-default-id (lambda () (.restoreDefaultId PurpleRobot)))
(define pr-scan-nfc (lambda () (.scanNFC PurpleRobot)))
(define pr-schedule-script (lambda (identifier date-string action) (.scheduleScript PurpleRobot identifier date-string action)))
(define pr-set-password (lambda (password) (.setPassword PurpleRobot password)))
(define pr-set-user-id (lambda args (apply .setUserId (cons PurpleRobot args))))
(define pr-scheduled-scripts (lambda () (.scheduledScripts PurpleRobot)))
(define pr-show-application-launch-notification (lambda args (apply .showApplicationLaunchNotification (cons PurpleRobot args))))
(define pr-show-native-dialog (lambda args (apply .showNativeDialog (cons PurpleRobot args))))
(define pr-show-script-notification (lambda args (apply .showScriptNotification (cons PurpleRobot args))))
(define pr-stop-playback (lambda () (.stopPlayback PurpleRobot)))
(define pr-stop-vibrate (lambda () (.stopVibrate PurpleRobot)))
(define pr-take-snapshot (lambda (source) (.takeSnapshot PurpleRobot source)))
(define pr-update-config (lambda args (apply .updateConfig (cons PurpleRobot args))))
(define pr-update-config-url (lambda (url) (.updateConfigUrl PurpleRobot url)))
(define pr-update-probe (lambda (probe-config) (.updateProbe PurpleRobot probe-config)))
(define pr-update-trigger (lambda (trigger-config) (.updateTrigger PurpleRobot trigger-config)))
(define pr-version (lambda () (.version PurpleRobot)))
(define pr-version-code (lambda () (.versionCode PurpleRobot)))
(define pr-vibrate (lambda args (apply .vibrate (cons PurpleRobot args))))

(define pr-update-widget (lambda (widget-config) (.updateWidget PurpleRobot widget-config)))
(define pr-date->components (lambda (date) (.dateToComponents PurpleRobot date)))
(define pr-components->date (lambda (action components) (.dateFromComponents PurpleRobot components)))
(define pr-nth (lambda (index pairs) (.nth PurpleRobot index pairs)))
