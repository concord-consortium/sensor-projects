#!/bin/sh
#
# We'll zip up the app, and then copy it to the redist folder
#
cd "${BUILT_PRODUCTS_DIR}"
ZIP_NAME="DeviceCheckBinaries.zip"

#
# First remove old zip files (if any)
#
rm -f *.zip
rm -f -r *.dSYM

#
# And Zip
#
zip -D $ZIP_NAME *

#
# Now copy to redist
#
if [ $CONFIGURATION == "Deployment" ]; then
	REDIST_FOLDER="${PROJECT_DIR}/../../../../redist/GoIO_DeviceCheck/MacOSX"
	#
	# Remove old file at destination:
	#
	if [ -f "${REDIST_FOLDER}/${ZIP_NAME}" ]; then
		rm "${REDIST_FOLDER}/${ZIP_NAME}"
	fi

	#
	# And move the new file:
	#
	cp "${ZIP_NAME}" "${REDIST_FOLDER}"
fi



