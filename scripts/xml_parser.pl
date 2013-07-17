#!/usr/bin/perl

# use module
use XML::Simple;
use Data::Dumper;

# create object
$xml = new XML::Simple;

my @files = <./test/*>;

foreach (@files)
{
	$data = $xml->XMLin($_);
	
	open FILE, ">:utf8",$_."test" or die $!;
	binmode(FILE, ":utf8");

	while ( my ($k, $v) = each %{$data->{lexelt}->{instance}} )
	{		
		print FILE $v->{context}->{content}[0] . " " . $v->{context}->{content}[1] . "\n";
	}
	
	close FILE;
}
