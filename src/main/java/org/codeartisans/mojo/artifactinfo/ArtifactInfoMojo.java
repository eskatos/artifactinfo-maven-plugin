/*
 * Copyright (c) 2011, Paul Merlin. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.codeartisans.mojo.artifactinfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @goal artifactinfo
 */
public class ArtifactInfoMojo
        extends AbstractMojo
{

    /**
     * @parameter default-value="false"
     */
    private boolean skip;
    /**
     * @parameter
     */
    private String packageName;
    /**
     * @parameter
     */
    private String className;
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    @Override
    @SuppressWarnings( "LocalVariableHidesMemberVariable" )
    public void execute()
            throws MojoExecutionException, MojoFailureException
    {
        if ( skip ) {
            getLog().info( "artifactinfo-maven-plugin execution is skipped" );
            return;
        }
        String packageName = resolvePackageName();
        String className = resolveClassName();
        try {
            String template = IOUtil.toString( getClass().getResourceAsStream( "ArtifactInfo.class.tpl" ), "UTF-8" );

            String output = template.replaceAll( "#packageName#", packageName );
            output = output.replaceAll( "#className#", className );
            output = output.replaceAll( "#groupId#", mavenProject.getGroupId() );
            output = output.replaceAll( "#artifactId#", mavenProject.getArtifactId() );
            output = output.replaceAll( "#version#", mavenProject.getVersion() );
            if ( !StringUtils.isEmpty( mavenProject.getDescription() ) ) {
                output = output.replaceAll( "#description#", mavenProject.getDescription() );
            }
            if ( !StringUtils.isEmpty( mavenProject.getName() ) ) {
                output = output.replaceAll( "#name#", mavenProject.getName() );
            }
            if ( !StringUtils.isEmpty( mavenProject.getUrl() ) ) {
                output = output.replaceAll( "#url#", mavenProject.getUrl() );
            }
            if ( !StringUtils.isEmpty( mavenProject.getInceptionYear() ) ) {
                output = output.replaceAll( "#inceptionYear#", mavenProject.getInceptionYear() );
            }

            File generatedSources = new File( new File( new File( mavenProject.getBuild().getDirectory() ), "generated-sources" ), "artifactinfo" );
            mavenProject.addCompileSourceRoot( generatedSources.getAbsolutePath() );

            for ( String eachSubDir : packageName.split( "\\." ) ) {
                generatedSources = new File( generatedSources, eachSubDir );
            }
            File javaFile = new File( generatedSources, className + ".java" );

            if ( generatedSources.exists() ) {
                FileUtils.deleteDirectory( generatedSources );
            }
            if ( !generatedSources.mkdirs() ) {
                throw new IOException( "Unable to create generated sources directory" );
            }
            IOUtil.copy( output, new FileOutputStream( javaFile ) );

            getLog().info( "Generated " + packageName + "." + className + " java source code in " + javaFile.getAbsolutePath() );

        } catch ( IOException ex ) {
            getLog().error( ex );
            throw new MojoExecutionException( "IOExsception during ArtifactInfo class generation", ex );
        }
    }

    private static final String PACKAGENAME_VALID_CHARS = "abcdefghijklmnopqrstuvwxyz.";

    private String resolvePackageName()
    {
        if ( !StringUtils.isEmpty( packageName ) ) {
            validatesName( packageName, PACKAGENAME_VALID_CHARS );
            return packageName;
        }
        StringBuilder sb = new StringBuilder();
        String nonFiltered = mavenProject.getGroupId().toLowerCase();
        nonFiltered = specialTrim( nonFiltered );
        for ( int idx = 0; idx < nonFiltered.length(); idx++ ) {
            char current = nonFiltered.charAt( idx );
            if ( PACKAGENAME_VALID_CHARS.indexOf( ( int ) current ) != -1 ) {
                sb.append( current );
            }
        }
        return sb.toString();
    }

    private static final String CLASSNAME_VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private String resolveClassName()
    {
        if ( !StringUtils.isEmpty( className ) ) {
            validatesName( className, CLASSNAME_VALID_CHARS );
            return className;
        }
        StringBuilder sb = new StringBuilder();
        String nonFiltered = mavenProject.getArtifactId();
        nonFiltered = specialTrim( nonFiltered );
        String previous = new String( new char[]{ nonFiltered.charAt( 0 ) } );
        sb.append( previous.toUpperCase() );
        for ( int idx = 1; idx < nonFiltered.length(); idx++ ) {
            char current = nonFiltered.charAt( idx );
            if ( CLASSNAME_VALID_CHARS.indexOf( ( int ) current ) != -1 ) {
                if ( CLASSNAME_VALID_CHARS.contains( previous ) ) {
                    sb.append( current );
                } else {
                    sb.append( new String( new char[]{ current } ).toUpperCase() );
                }
            }
            previous = new String( new char[]{ current } );
        }
        sb.append( "_ArtifactInfo" );
        return sb.toString();
    }

    private void validatesName( String name, String allowed )
    {
        for ( int idx = 0; idx < name.length(); idx++ ) {
            char current = name.charAt( idx );
            if ( allowed.indexOf( ( int ) current ) == -1 ) {
                throw new IllegalArgumentException( "Given name [" + name + "] contains unallowed char [" + current + "], allowed are [" + allowed + ']' );
            }
        }
    }

    /**
     * Trim and remove trailing dots.
     */
    private String specialTrim( String str )
    {
        str = str.trim();
        if ( str.startsWith( "." ) ) {
            str = str.substring( 1 );
        }
        if ( str.endsWith( "." ) ) {
            str = str.substring( 0, str.length() - 1 );
        }
        return str;
    }

}
