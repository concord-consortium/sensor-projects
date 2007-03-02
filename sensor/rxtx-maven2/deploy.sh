#this should be turned into a plugin, or we should try to use 
#already existing nar plugin

cd $1
source properties

DEPLOY_URL=scpexe://source.concord.org/web/source.concord.org/html/software/maven2/internal/
DEPLOY_ID=cc-dist-repo-internal
CLASSIFIER=${PLATFORM}
NAR_FILE=../../target/${ARTIFACT_ID}-${PLATFORM}.nar

# need to build the nar file of the linux native libraries
jar cf ${NAR_FILE} ${NATIVE_FILES}

mvn deploy:deploy-file -Dfile=${NAR_FILE} \
    -DartifactId=${ARTIFACT_ID} -DgroupId=${GROUP_ID} \
    -Dclassifier=${CLASSIFIER} \
    -Dversion=${VERSION} -Dpackaging=nar \
    -Durl=${DEPLOY_URL} -DrepositoryId=${DEPLOY_ID}

cd -
