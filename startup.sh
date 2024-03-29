#!/bin/bash

mvn clean install dependency:copy-dependencies -DincludeScope=compile
path=""
for f in $(find target -name "*.jar"); do
	path="$path:$f"
done
module_path="/usr/local/lib/javafx/lib/"
modules="javafx.controls,javafx.fxml"
main_class="dev.cnpuvache.gba.tile_manager.StartUp"
# main_class="dev.cnpuvache.gba.tile_manager.Test"

java \
	-cp "$path" \
	--module-path $module_path \
	--add-modules $modules \
		$main_class
	# -Dprism.verbose=true \
