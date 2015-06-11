require 'compass'

Compass.add_configuration(
           {
            :additional_import_paths => $additional_import_paths,
            :project_path => $project_path,
            :sass_path => $sass_path,
            :css_path => $css_path,
            :images_path => $images_path,
            :line_comments => false,
           },
           'custom'
)
Compass.compiler.compile($sass_file, $css_file)



