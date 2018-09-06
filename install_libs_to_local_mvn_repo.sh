echo "Installing needed libraries to local maven repository"

cd driver/zwave
sh install-libzwave.sh
cd ../cul
sh install-lib-cul.sh
cd ../..
cd services/connctd
sh install-connector-sdk.sh
cd ../..