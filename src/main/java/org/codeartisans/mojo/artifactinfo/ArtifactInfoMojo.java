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
import java.util.Date;
import java.util.Locale;

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
    private String packageName = "";

    /**
     * @parameter
     */
    private String className = "";

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
        String packageName = new ResolvePackageName(this.packageName, mavenProject.getGroupId()).resolve();
        String className = new ResolveClassName(this.className, mavenProject.getArtifactId()).resolve();
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
            output = output.replaceAll( "#buildTimestamp#", System.currentTimeMillis() + "" );

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
    
    /**
     * Abstract Resolver class with helper functions for validation.
     */
    static abstract class Resolver {
        
        final String validCharacters;
        
        Resolver(final String validCharacters) {
            this.validCharacters = validCharacters;            
        }
        
        void validatesName( String name )
        {
            for ( int idx = 0; idx < name.length(); idx++ ) {
                char current = name.charAt( idx );
                if ( validCharacters.indexOf( ( int ) current ) == -1 ) {
                    throw new IllegalArgumentException( "Given name [" + name + "] contains unallowed char [" + current + "], allowed are [" + validCharacters + ']' );
                }
            }
        }

        /**
         * Trim and remove trailing dots.
         */
        String specialTrim( String str )
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
        
        abstract String resolve();
    }
    
    static class ResolvePackageName extends Resolver {
        
        private final String givenPackageName;
        private final String groupId;

        public ResolvePackageName(final String givenPackageName, final String groupId) {
            super("abcdefghijklmnopqrstuvwxyz.");
            this.givenPackageName = givenPackageName;
            this.groupId = groupId.toLowerCase(Locale.ENGLISH);
        }
        
        String resolve()
        {
            if ( !StringUtils.isEmpty( givenPackageName ) ) {
                validatesName( givenPackageName );
                return givenPackageName;
            }
            StringBuilder sb = new StringBuilder();
            String nonFiltered = groupId;
            nonFiltered = specialTrim( nonFiltered );
            for ( int idx = 0; idx < nonFiltered.length(); idx++ ) {
                char current = nonFiltered.charAt( idx );
                if ( validCharacters.indexOf( ( int ) current ) != -1 ) {
                    sb.append( current );
                }
            }
            return sb.toString();
        }
    }

    static class ResolveClassName extends Resolver {
        
        private final String givenClassName;
        private final String artifactId;

        public ResolveClassName(final String givenClassName, final String artifactId) {
            super("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
            this.givenClassName = givenClassName;
            this.artifactId = artifactId;
        }
    
        String resolve()
        {
            if ( !StringUtils.isEmpty( givenClassName ) ) {
                validatesName( givenClassName );
                return givenClassName;
            }
            StringBuilder sb = new StringBuilder();
            String nonFiltered = artifactId;
            nonFiltered = specialTrim( nonFiltered );
            String previous = new String( new char[]{ nonFiltered.charAt( 0 ) } );
            sb.append( previous.toUpperCase() );
            for ( int idx = 1; idx < nonFiltered.length(); idx++ ) {
                char current = nonFiltered.charAt( idx );
                if ( validCharacters.indexOf( ( int ) current ) != -1 ) {
                    if ( validCharacters.contains( previous ) ) {
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
    }
}
