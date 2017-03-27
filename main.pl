#!/usr/bin/perl -w

# prints out all the lists of length 0 to 2 which hold either
# true or false for each element

use ListIterator;
use ArrayIterator;

use warnings;
use strict;

# BEGIN MAIN CODE

# We want the words 'true' and 'false', in an array
my @words = ('true', 'false');

# A constructor for iterators over arrays.  Simply creates
# a new array iterator when called.  'sub' allows for the
# creation of named and anonymous functions
my $constructor = sub {
    return new ArrayIterator(@words);
};

# Creates lists of length 0 to 2, where the contents are
# populated by the iterator returned from calling
# '$constructor'
my $listIterator = new ListIterator(0, 2, $constructor);
$listIterator->foreachElement(
    sub {
        my $arrRef = shift(); # the parameter (a list)
        print '[' . join(', ', @$arrRef) . "]\n";
    });
$listIterator->reset(); # not necessary, but good practice
