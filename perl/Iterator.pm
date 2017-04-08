#!/usr/bin/perl -w

package Iterator;

use warnings;
use strict;


sub new {
    my $class = shift();
    my $self = {};
    bless($self, $class);
    return $self;
}

sub hasNext {
    die "Abstract method hasNext called";
}

sub getNext {
    die "Abstract method getNext called";
}

sub reset {
    die "Abstract method reset called";
}

# Runs through the rest of this iterator.
# It is the caller's responsibility to call reset()
# if so desired.
sub foreachElement($$) {
    my ($self, $f) = @_;
    while ($self->hasNext()) {
        $f->($self->getNext());
    }
}

1;
