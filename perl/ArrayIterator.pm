#!/usr/bin/perl -w

package ArrayIterator;

use Iterator;
use warnings;
use strict;

our @ISA = qw(Iterator);

# Takes an array of items to iterate over
sub new {
    my ($class, @items) = @_;
    my $self = $class->SUPER::new();
    $self->{_position} = 0;
    $self->{_array} = \@items;
    bless($self, $class);
    return $self;
}

sub numItems {
    my $self = shift();
    return scalar(@{$self->{_array}});
}

sub hasNext {
    my $self = shift();
    return $self->{_position} < $self->numItems();
}

sub getNext {
    my $self = shift();
    if (!$self->hasNext()) {
        die "No more items";
    }
    
    my $pos = $self->{_position};
    my $retval = $self->{_array}->[$pos];
    $self->{_position} = $pos + 1;

    return $retval;
}

sub reset {
    my $self = shift();
    $self->{_position} = 0;
}

1;
